package com.argelaa.fstapi.service;

import com.argelaa.common.OrderPlacedEvent;
import com.argelaa.fstapi.model.Product;
import com.argelaa.fstapi.repository.ProductRepository;

import org.springframework.stereotype.Service; /* @Service notasyonunu kullanmak için */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional; /* Bir değerin var olup olmadığını güvenli bir şekilde tutmak için */

/*
	@Service ile Spring Boot'a bu sınıfın bir iş mantığı (service) olduğunu
	söyleriz. Böylece bu sınıfı bir bean olarak yönetmeye başlar.
*/
@Service
public class ProductService
{
	private final ProductRepository productRepository;
	private final KafkaProducerService kafkaProducerService; // Kafka servisini ekliyoruz

	@Autowired
	public ProductService(ProductRepository productRepository, KafkaProducerService kafkaProducerService) // Constructor'a ekliyoruz
	{
		this.productRepository = productRepository;
		this.kafkaProducerService = kafkaProducerService;
	}

	public List<Product> getAllProducts()
	{
		return productRepository.findAll();
	}

	/*
		Aranacak data'nın bulunamama ihtimaline karşı Optional denen bir yapı
		kullanılır. Yani Optional<Product>, bu metodun bir Product döndürebileceğini
		(burada aslında Product nesnesinin referans değişkeni döndürülür, daha doğru
		bir tabirle) veya hiçbir şey içermeyebileceğini anlatır. NullPointerException
		hatalarının compile-time'da fark edilmesine yardımcı olur NullPointerException
		hatalarını azaltmak ve programı daha güvenli hale getirmek için kullanılır.
	*/
	public Optional<Product> getProductById(Long id)
	{
		return productRepository.findById(id);

		/*
			Optional, map(), orElse(), ifPresent() gibi kendi metotlarına sahiptir.
			Bu metotlar sayesinde, if (x != null) gibi geleneksel null kontrollerinden
			daha temiz ve daha işlevsel kod yazılabilir.

			- productOptional.isPresent(), eğer Product varsa true döner.
			- productOptional.get() ile o ürüne ulaşılır ama önce isPresent ile varlığı
			test edilmelidir.

			- stream() metodu, java.util.Collection interface'inin bir metodudur.
			ArrayList de bir Collection olduğu için bu metodu kullanabilir. stream()
			metodu, bir koleksiyonu (bizim durumumuzda products listesi) bir Stream'e
			dönüştürür. Stream, Java 8 ile gelen ve koleksiyonlar üzerinde (liste, küme
			vb.) daha kolay ve güçlü işlemler yapmayı sağlayan bir soyutlamadır. Veriler
			üzerinde sıralı veya paralel işlemler zinciri kurmanızı sağlar. Bir Stream'i,
			verilerin "aktığı" bir boru hattı gibi düşünebilirsiniz. Her işlem
			(filtreleme, dönüştürme, bulma) bu boru hattında gerçekleşir.

			- filter() metodu, bir Stream'deki öğeleri belirli bir koşula göre filtrelemek
			için kullanılır. Sadece bu koşulu sağlayan öğeler Stream'in bir sonraki
			aşamasına geçer.
				* Bu örnekteki koşul da şudur: p -> p.getId().equals(id), p streamde o anki
				nesneyi temsil eden geçici bir değişkendir.
				* ->, lambda ifadesinin sözdimsel ayıracıdır.
				* p.getId().equals(id), filtreleme koşuludr.

			- findFirst() metodu, filtreleme (veya diğer Stream işlemleri) sonucunda elde
			edilen Stream'deki ilk öğeyi döndürür. Ancak bunu bir Optional içinde döndürür.
			Çünkü Stream boş olabilir (hiçbir öğe koşulu sağlamamış olabilir) ve bu durumda
			hiçbir "ilk" öğe olmaz.
		*/
	}

	/* Create */
	public Product addProduct(Product product)
	{
		return productRepository.save(product);
	}

	/* Update */
	public Optional<Product> updateProduct(Long id, Product updatedProduct)
	{
		return productRepository.findById(id).map(existingProduct -> {
			existingProduct.setName(updatedProduct.getName());
			existingProduct.setPrice(updatedProduct.getPrice());
			existingProduct.setQuantity(updatedProduct.getQuantity());
			existingProduct.setCategory(updatedProduct.getCategory());

			return productRepository.save(existingProduct); // Güncellenmiş ürünü veritabanına kaydediyoruz
		});
	}

	/* Delete */
	public boolean deleteProduct(Long id)
	{
		if (productRepository.existsById(id)) { // Ürünün veritabanında var olup olmadığını kontrol ediyoruz
			productRepository.deleteById(id); // ID'ye göre ürünü veritabanından siliyoruz
			return true;
		}
		return false;
	}

	/* Yeni metot: Ürün satın alma işlemi */
	@Transactional // Bu işlem atomik olmalı: ya hepsi başarılı olur ya da hiçbiri.
	public Optional<Product> purchaseProduct(Long productId, Long customerId, Long quantity) {
		return productRepository.findById(productId).flatMap(product -> {
			if (product.getQuantity() >= quantity) {
				// Ürün stoğunu güncelle
				product.setQuantity(product.getQuantity() - quantity);
				Product updatedProduct = productRepository.save(product);

				// Kafka'ya olayı gönder
				OrderPlacedEvent event = new OrderPlacedEvent(
						productId,
						customerId,
						quantity,
						product.getPrice()
				);
				kafkaProducerService.sendOrderEvent(event);

				return Optional.of(updatedProduct);
			} else {
				// Yeterli stok yoksa boş Optional döndür
				return Optional.empty();
			}
		});
	}
}

// HTTP 2'ye çevir