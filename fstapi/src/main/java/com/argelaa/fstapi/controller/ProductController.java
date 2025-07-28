package com.argelaa.fstapi.controller;

import com.argelaa.fstapi.model.Product;
import com.argelaa.fstapi.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired; /* Bağımlılık Enjeksiyonu için */
import org.springframework.http.HttpStatus;   /* HTTP durum kodları için */
import org.springframework.http.ResponseEntity; /* HTTP yanıtını özelleştirmek için */
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController
{
	/*
		Final nedir?

		Java'da final anahtar kelimesi, bir şeyin son halini aldığını ve değiştirilemez 
		(immutable) olduğunu belirtmek için kullanılır. Bir değişken final olarak 
		tanımlandığında, o değişkene yalnızca bir kez değer atanabilir. Bu değer atandıktan 
		sonra bir daha değiştirilemez.
	*/
	private final ProductService productService;

	/*
		@Autowired notasyonu, Spring Framework'ün Bağımlılık Enjeksiyonu (Dependency Injection - DI) 
		mekanizmasının bir parçasıdır. Bu notasyon, bir sınıfın (Component, Service, Controller, 
		Repository gibi Spring tarafından yönetilen bir "Bean"in) ihtiyaç duyduğu başka bir bağımlılığı, 
		Spring IoC (Inversion of Control) Konteyneri tarafından otomatik olarak sağlanmasını (enjekte 
		edilmesini) talep etmek için kullanılır.

		ProductController sınıfımız, ProductService sınıfının bir örneğine (productService değişkeni) 
		ihtiyaç duymaktadır. Bu durum, ProductController'ın ProductService'e bir bağımlılığı olduğunu 
		gösterir.

		Bu notasyon, Spring IoC Konteynerine şunu bildirir: "Hey Spring, bu ProductController bean'ini 
		oluştururken, lütfen bana ProductService türünde bir bağımlılık sağla." Spring Konteyneri, 
		@Autowired isteğini aldığında, kendi bünyesinde (yani konteynerde) zaten var olan veya henüz 
		oluşturmadığı ancak oluşturabileceği bir ProductService bean'i arar.

		Daha önce ProductService sınıfını @Service notasyonu ile işaretlediğimiz için, Spring bu sınıfın 
		bir bean olduğunu bilir ve uygulamanın başlangıcında onun bir örneğini oluşturur.
		
		```
		@Service
		public class ProductService
		{
			...
		}
		```

		Spring, ProductService bean'inin tekil bir örneğini (singleton scope varsayılan olarak) bulur ve 
		bu örneği ProductController'ın yapıcı metoduna parametre olarak geçirir. Yapıcı metot içindeki 
		this.productService = productService; satırı ile, Spring tarafından sağlanan ProductService 
		örneği, ProductController'ın productService alanına atanır.
	*/
	@Autowired
	public ProductController(ProductService productService)
	{
		this.productService = productService;
	}

	@GetMapping
	public ResponseEntity<List<Product>> getAllProducts()
	{
		List<Product> products = productService.getAllProducts();
		return new ResponseEntity<>(products, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable Long id)
	{
		Optional<Product> product = productService.getProductById(id);

		if (product.isPresent()) {
			Product exProduct = product.get();

			return new ResponseEntity<>(exProduct, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		/*
		return productService.getProductById(id)
                .map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    	*/
	}

	@PostMapping
	public ResponseEntity<Product> addProduct(@RequestBody Product product)
	{
		Product newProduct = productService.addProduct(product);
		return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product)
	{
		return productService.updateProduct(id, product)
				.map(updateProd -> new ResponseEntity<>(updateProd, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long id)
	{
		if (productService.deleteProduct(id)) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}
