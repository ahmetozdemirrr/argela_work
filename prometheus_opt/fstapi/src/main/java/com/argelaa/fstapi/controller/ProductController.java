package com.argelaa.fstapi.controller;

import com.argelaa.fstapi.model.Product;
import com.argelaa.fstapi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	@GetMapping
	public ResponseEntity<List<Product>> getAllProducts() {
		List<Product> products = productService.getAllProducts();
		return new ResponseEntity<>(products, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable Long id) {
		return productService.getProductById(id)
				.map(product -> new ResponseEntity<>(product, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping
	public ResponseEntity<Product> addProduct(@RequestBody Product product) {
		Product newProduct = productService.addProduct(product);
		return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
	}

	/**
	 * YENİ EKLENEN ENDPOINT
	 * Bir ürünün satın alınması işlemini gerçekleştirir.
	 * Örnek istek: POST http://localhost:8080/api/products/1/purchase?customerId=123&quantity=5
	 *
	 * @param productId   Satın alınacak ürünün ID'si (URL'den alınır).
	 * @param customerId  Satın alan müşteri ID'si (Query parametresi).
	 * @param quantity    Satın alınacak miktar (Query parametresi).
	 * @return            İşlem başarılıysa güncellenmiş ürün bilgisi, değilse hata döner.
	 */
	@PostMapping("/{productId}/purchase")
	public ResponseEntity<Product> purchaseProduct(
			@PathVariable Long productId,
			@RequestParam Long customerId,
			@RequestParam Long quantity) {

		return productService.purchaseProduct(productId, customerId, quantity)
				.map(updatedProduct -> new ResponseEntity<>(updatedProduct, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST)); // Stok yetersizse veya ürün bulunamazsa
	}


	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
		if (productService.deleteProduct(id)) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}