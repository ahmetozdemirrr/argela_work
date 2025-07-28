package com.argelaa.fstapi.service;

import com.argelaa.fstapi.model.Product;

import org.springframework.stereotype.Service; /* @Service notasyonunu kullanmak için */

import java.util.ArrayList;
import java.util.List;
import java.util.Optional; /* Bir değerin var olup olmadığını güvenli bir şekilde tutmak için */
import java.util.concurrent.atomic.AtomicLong; /* Basit ID üretici */

/* 
	@Service ile Spring Boot'a bu sınıfın bir iş mantığı (service) olduğunu
	söyleriz. Böylece bu sınıfı bir bean olarak yönetmeye başlar.
*/
@Service
public class ProductService
{
	/* ürünleri arrayList olarak saklayacağız */
	private final List<Product> products = new ArrayList<>();
	/* her ürüne benzersiz bir id üretir */
	private final AtomicLong counter = new AtomicLong();

	public ProductService()
	{
		products.add(new Product(counter.incrementAndGet(), "Laptop", 1200.00));
		products.add(new Product(counter.incrementAndGet(), "Mouse",    25.00));
		products.add(new Product(counter.incrementAndGet(), "Keyboard", 75.00));
	}

	public List<Product> getAllProducts()
	{
		return products;
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
		return products
				.stream()
				.filter(p -> p.getId().equals(id))
				.findFirst();

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
		product.setId(counter.incrementAndGet());
		products.add(product);

		return product;
	}

	/* Update */
	public Optional<Product> updateProduct(Long id, Product updatedProduct)
	{
		Optional<Product> existingProductOpt = getProductById(id);

		if (existingProductOpt.isPresent()) {
			Product existingProduct = existingProductOpt.get();

			existingProduct.setName(updatedProduct.getName());
			existingProduct.setPrice(updatedProduct.getPrice());

			return Optional.of(existingProduct);
		}
		return Optional.empty();
	}

	/* Delete */
	public boolean deleteProduct(Long id)
	{
		return products.removeIf(p -> p.getId().equals(id));
	}
}

// HTTP 2'ye çevir
