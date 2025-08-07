package com.argelaa.fstapi.service;

import com.argelaa.common.OrderPlacedEvent;
import com.argelaa.fstapi.model.Product;
import com.argelaa.fstapi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private KafkaProducerService kafkaProducerService;

	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	public Optional<Product> getProductById(Long id) {
		return productRepository.findById(id);
	}

	public Product addProduct(Product product) {
		return productRepository.save(product);
	}

	public boolean deleteProduct(Long id) {
		if (productRepository.existsById(id)) {
			productRepository.deleteById(id);
			return true;
		}
		return false;
	}

	@Transactional
	public Optional<Product> purchaseProduct(Long productId, Long customerId, Long quantity) {
		return productRepository.findById(productId).flatMap(product -> {
			if (product.getQuantity() >= quantity) {
				product.setQuantity(product.getQuantity() - quantity);
				Product updatedProduct = productRepository.save(product);

				// Kafka'ya olayı gönder (Kategori bilgisi eklendi)
				OrderPlacedEvent event = new OrderPlacedEvent(
						productId,
						customerId,
						quantity,
						product.getPrice(),
						product.getCategory()
				);
				kafkaProducerService.sendOrderEvent(event);

				return Optional.of(updatedProduct);
			} else {
				System.out.println("Stok yetersiz. Talep edilen: " + quantity + ", Mevcut: " + product.getQuantity());
				return Optional.empty();
			}
		});
	}
}