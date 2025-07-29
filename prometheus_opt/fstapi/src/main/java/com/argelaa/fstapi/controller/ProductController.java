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

	/*
		ResponseEntity sınıfı, Spring Framework'te bir HTTP yanıtının tamamını temsil eden bir yapıdır.
		Yani, bir REST API'den istemciye geri dönen yanıtın gövdesini (body), HTTP durum kodunu (status
		code) ve isteğe bağlı olarak başlıklarını (headers) tamamen kontrol etmenizi sağlar.

		- products: ResponseEntity'nin ilk parametresi, HTTP yanıtının gövdesini oluşturacak olan veridir.
		Burada productService.getAllProducts() metodundan dönen List<Product> nesnesi kullanılır. Spring
		Boot, bu List<Product>'ı otomatik olarak JSON formatına dönüştürecektir.

		- HttpStatus.OK: ResponseEntity'nin ikinci parametresi, HTTP yanıtının durum kodunu belirler.
		HttpStatus.OK (200) HTTP durum kodu, isteğin başarıyla tamamlandığını ve tüm ürünlerin sorunsuz bir
		şekilde bulunduğunu ve döndürüldüğünü gösterir.
	 */

	/*
		HttpStatus bir Java enum'ıdır (enumeration). Enum'lar, sabit bir değer kümesini temsil etmek için
		kullanılır. HttpStatus enum'ı da HTTP protokolünde tanımlanmış standart durum kodlarını (status codes)
		ve bunlara karşılık gelen açıklamaları içerir:

		- HttpStatus.OK: HTTP durum kodu 200'ü temsil eder. Bu, isteğin başarıyla işlendiği anlamına gelir.
		Genellikle GET istekleri için başarılı bir yanıtı belirtir.

		- HttpStatus.CREATED: HTTP durum kodu 201'i temsil eder. Bu, isteğin başarıyla yerine getirildiği ve
		sonucunda yeni bir kaynağın oluşturulduğu anlamına gelir. Genellikle POST istekleri (yeni kaynak
		oluşturma) için kullanılır.

		- HttpStatus.NOT_FOUND: HTTP durum kodu 404'ü temsil eder. Bu, sunucunun istenen kaynağı bulamadığı
		anlamına gelir. Yani istemcinin talep ettiği URL'ye karşılık gelen bir şeyin sunucuda olmadığı durumlar
		için kullanılır.

		- HttpStatus.NO_CONTENT: HTTP durum kodu 204'ü temsil eder. Bu, sunucunun isteği başarıyla işlediği
		ancak yanıt gövdesinde (response body) geri döndürecek bir içerik olmadığı anlamına gelir. Genellikle
		DELETE istekleri gibi durumlarda, işlem başarılı olduğunda ancak istemciye bir veri dönülmesine gerek
		olmadığında kullanılır.

		- HttpStatus.BAD_REQUEST: HTTP durum kodu 400'ü temsil eder. Bu, sunucunun istemci hatası nedeniyle
		isteği işleyemediği anlamına gelir. Örneğin, geçersiz bir istek formatı veya eksik parametreler gibi
		durumlarda kullanılır.

		- HttpStatus.UNAUTHORIZED: HTTP durum kodu 401'i temsil eder. Bu, isteğin kimlik doğrulama gerektirdiği
		ve henüz yapılmadığı veya başarısız olduğu anlamına gelir.

		- HttpStatus.FORBIDDEN: HTTP durum kodu 403'ü temsil eder. Bu, sunucunun isteği anladığı ancak
		yetkilendirme eksikliği nedeniyle reddettiği anlamına gelir. Kullanıcının bu kaynağa erişim izni
		yoksa kullanılır.

		- HttpStatus.INTERNAL_SERVER_ERROR: HTTP durum kodu 500'ü temsil eder. Bu, sunucunun isteği yerine
		getirirken beklenmeyen bir durumla karşılaştığı anlamına gelir. Genellikle sunucu tarafında bir hata
		oluştuğunda kullanılır.
	 */
	@GetMapping
	public ResponseEntity<List<Product>> getAllProducts()
	{
		List<Product> products = productService.getAllProducts();
		return new ResponseEntity<>(products, HttpStatus.OK);
	}

	/*
		@GetMapping aslında @RequestMapping(method = RequestMethod.GET) notasyonunun kısaltılmış (syntactic
		sugar) halidir. Bir HTTP GET isteğini belirli bir metoda eşlemek için kullanılır. Yani, istemci bir
		kaynağı sunucudan almak istediğinde bu metoda yönlendirilir.

		("/{id}") Ne İfade Eder?

		Bu kısım, URL yolundaki (path) bir değişkeni (variable) belirtir. {id} buradaki {} içine alınan id
		kısmı bir URI şablon değişkenidir (URI Template Variable). Bu, URL'nin dinamik bir bölümü olacağı
		anlamına gelir. Örneğin, /api/products/1, /api/products/123 gibi istekler geldiğinde, 1 veya 123
		değeri {id} yerine geçecektir.

		@GetMapping("/{id}") ile tanımlanan URI şablon değişkeni ({id}), ilgili metodun parametresine
		@PathVariable notasyonu ile bağlanır. @PathVariable Long id ifadesi, URL'deki {id} değerinin Long
		tipinde bir id parametresine dönüştürülmesini sağlar. Spring, gelen isteğin URL'sindeki {id} kısmını
		alıp, metot parametresindeki id değişkenine otomatik olarak atar. Örnek olarak, /api/products/5
		adresine yapılan bir GET isteği geldiğinde, getProductById metodunun içindeki id parametresinin
		değeri 5 olacaktır.
	 */
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

	/*
		@RequestBody notasyonu, Spring Framework'te bir HTTP isteğinin gövdesini (request body) doğrudan
		bir Java nesnesine bağlamak (bind etmek) için kullanılır. Özellikle RESTful API'lerde istemciden
		gelen JSON veya XML gibi verileri otomatik olarak Java objelerine dönüştürmek için çok yaygın bir
		şekilde kullanılır.

		Bu sürece "deserialization" denir.

		{
			"name": "Yeni Ürün Adı",
			"price": 99.99
		}

		@RequestBody Product product sayesinde, Spring bu JSON verisini ayrıştırır ve name'i "Yeni Ürün
		Adı", price'ı 99.99 olan yeni bir Product nesnesi oluşturup, bu product parametresine atar. Product
		sınıfında uygun getter ve setter metotlarının (setName, setPrice) olması bu dönüşüm için gereklidir.
		@RequestBody ile birlikte @Valid gibi doğrulama notasyonları da kullanılabilir. Bu, gelen verinin
		belirli kurallara uygun olup olmadığını otomatik olarak kontrol etmeye yardımcı olur (örneğin, ürün
		adının boş olmaması veya fiyatın pozitif olması gibi).
	 */
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
