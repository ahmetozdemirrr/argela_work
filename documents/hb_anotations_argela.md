### Spring Boot REST API Geliştiricisi İçin Temel Anotasyonlar (Detaylı Teknik Rehber)



Bu rehber, anotasyonları mantıksal gruplar halinde sunar.

------



### I. Spring Core / IoC (Inversion of Control - Kontrolün Tersine Çevrilmesi) Container ve Bağımlılık Enjeksiyonu (Dependency Injection) Anotasyonları



Spring Framework'ün kalbini oluşturan bu modül, nesnelerin oluşturulmasını, yaşam döngüsünü ve birbirleriyle olan ilişkilerini yönetir.

1. **`@Component`**

   - **Tanım:** Spring'e, bu sınıfın Spring IoC Container (Inversion of Control Container - Uygulamanın nesnelerini oluşturan, bir araya getiren ve yöneten Spring modülü) tarafından yönetilmesi gereken bir bileşen (component) olduğunu bildiren genel amaçlı bir stereotip anotasyondur. `@Component` ile işaretlenen sınıflar, Spring'in "component scanning" (bileşen tarama - uygulamadaki `@Component` ve benzeri anotasyonlara sahip sınıfları otomatik olarak bulma ve Bean olarak kaydetme) mekanizması tarafından otomatik olarak Bean (bir Spring uygulaması tarafından yönetilen ve Spring IoC Container tarafından yaratılan ve/veya yönetilen nesneler) olarak algılanır ve kaydedilir.

   - **Kullanım Alanı:** Genel Spring bileşenleri için, genellikle daha spesifik stereotip anotasyonların (`@Service`, `@Repository`, `@Controller`) uymadığı durumlarda kullanılır.

   - **Örnek:**

     Java

     ```java
     package com.example.demo.util;
     
     import org.springframework.stereotype.Component;
     
     @Component // Bu sınıf bir Spring Bean'idir ve Spring tarafından yönetilir.
     public class AppLogger {
         public void logInfo(String message) {
             System.out.println("[INFO] " + message);
         }
     }
     ```

2. **`@Service`**

   - **Tanım:** Bir sınıfın iş katmanı (Business Layer) bileşeni olduğunu belirtmek için kullanılan bir stereotip anotasyondur. Aslında `@Component`'in özelleşmiş bir halidir, ancak kodun okunabilirliğini artırır ve Spring'in gelecekte bu katmana özel davranışlar eklemesine olanak tanır. İş mantığı, veri işleme kuralları gibi süreçleri barındırır.

   - **Kullanım Alanı:** Uygulamanın iş mantığını içeren servis sınıfları için.

   - **Örnek:**

     Java

     ```java
     package com.example.demo.service;
     
     import org.springframework.stereotype.Service;
     import com.example.demo.util.AppLogger; // AppLogger'ı kullanacağız
     
     @Service // Bu sınıf bir iş servisi Bean'idir.
     public class ProductService {
     
         private final AppLogger logger; // AppLogger bağımlılığı
     
         // @Autowired'e gerek yok Java 17+ ve tek constructor varsa
         public ProductService(AppLogger logger) { // Spring, AppLogger Bean'ini buraya enjekte eder (Dependency Injection)
             this.logger = logger;
         }
     
         public String getProductDetails(String productId) {
             logger.logInfo("Fetching details for product: " + productId);
             // Burada ürünle ilgili karmaşık iş mantığı olabilir
             return "Details for Product " + productId;
         }
     }
     ```

3. **`@Repository`**

   - **Tanım:** Veri erişim katmanı (Data Access Layer - Uygulamanın veritabanı veya diğer kalıcı depolama birimleriyle etkileşim kurduğu katman) bileşeni olduğunu belirtmek için kullanılan bir stereotip anotasyondur. `@Component`'in bir alt türüdür. Spring Data JPA ile kullanıldığında, belirli veritabanı erişim istisnalarını Spring'in veri erişim istisnalarına (örn. `DataAccessException`) dönüştürme gibi özel yetenekler sunar.

   - **Kullanım Alanı:** Veritabanı işlemleri (CRUD - Create, Read, Update, Delete) yapan sınıflar için.

   - **Örnek:**

     Java

     ```java
     package com.example.demo.repository;
     
     import org.springframework.stereotype.Repository;
     import com.example.demo.model.Product; // Product model sınıfı varsayılıyor
     import java.util.Optional;
     import java.util.ArrayList;
     import java.util.List;
     
     @Repository // Bu sınıf bir veri erişim katmanı Bean'idir.
     public class ProductRepository {
     
         private final List<Product> products = new ArrayList<>(); // Basit bir mock veritabanı
     
         public ProductRepository() {
             products.add(new Product(1L, "Laptop", 1200.0));
             products.add(new Product(2L, "Mouse", 25.0));
         }
     
         public Optional<Product> findById(Long id) {
             return products.stream()
                            .filter(p -> p.getId().equals(id)) // Stream (Java 8 ile gelen koleksiyonları daha işlevsel manipüle etme aracı) üzerinde filtreleme
                            .findFirst(); // Koşulu sağlayan ilk elemanı Optional içinde döndür
         }
     
         public List<Product> findAll() {
             return new ArrayList<>(products);
         }
     }
     ```

4. **`@Autowired`**

   - **Tanım:** Spring'e, bir sınıfın (Bean'in) başka bir Bean'e bağımlılığı olduğunu ve bu bağımlılığın Spring IoC Container tarafından otomatik olarak "enjekte edilmesi" (Dependency Injection - Bağımlılık Enjeksiyonu: Bir nesnenin bağımlılıklarını kendisinin oluşturmak veya aramak yerine, dışarıdan (Spring Container tarafından) sağlanması prensibi) gerektiğini bildiren anotasyondur. Constructor (yapıcı metot), setter metot veya alan (field) seviyesinde kullanılabilir. Java 17 ve sonrası ile tek constructor'a sahip sınıflarda constructor'a `@Autowired` eklemek zorunlu değildir, Spring otomatik algılar.

   - **Kullanım Alanı:** Bir Bean'in başka bir Bean'i kullanması gerektiğinde.

   - **Örnek:** (Yukarıdaki `ProductService` örneğinde gösterildi, burada alan seviyesinde tekrar edelim)

     Java

     ```java
     package com.example.demo.service;
     
     import org.springframework.beans.factory.annotation.Autowired; // Gerekli import
     import org.springframework.stereotype.Service;
     import com.example.demo.repository.ProductRepository; // ProductRepository bağımlılığı
     
     @Service
     public class ProductService {
     
         @Autowired // Spring, ProductRepository türündeki Bean'i buraya enjekte edecek
         private ProductRepository productRepository;
     
         public Optional<Product> getProductById(Long id) {
             return productRepository.findById(id);
         }
     }
     ```

5. **`@Bean`**

   - **Tanım:** Bir metodu işaretler. Spring'e, bu metodun döndürdüğü nesnenin bir Spring Bean'i olarak IoC Container'a kaydedilmesi gerektiğini söyler. Genellikle `@Configuration` anotasyonu ile işaretlenmiş sınıflar içinde kullanılır. Bu sayede, Spring'in otomatik olarak algılayamadığı (örneğin üçüncü parti kütüphanelerden gelen) veya özelleştirilmiş şekilde oluşturulması gereken nesneler Bean olarak tanımlanabilir.

   - **Kullanım Alanı:** Manuel olarak Bean tanımlamak veya özelleştirilmiş Bean'ler sağlamak için.

   - **Örnek:**

     Java

     ```java
     package com.example.demo.config;
     
     import org.springframework.context.annotation.Bean; // Gerekli import
     import org.springframework.context.annotation.Configuration;
     
     @Configuration // Bu sınıf, Bean tanımları içerir.
     public class AppConfig {
     
         @Bean // Bu metodun döndürdüğü 'DatabaseConnection' nesnesi bir Spring Bean'i olacaktır.
         public String databaseConnection() {
             // Burada bir veritabanı bağlantısı objesi oluşturulduğu varsayılır
             return "Database Connection Established!";
         }
     }
     
     // Başka bir yerde bu Bean'i kullanma:
     // @Service
     // public class MyService {
     //     private final String connection;
     //
     //     public MyService(@Autowired String connection) { // databaseConnection Bean'i enjekte edilir
     //         this.connection = connection;
     //         System.out.println(connection);
     //     }
     // }
     ```

6. **`@Configuration`**

   - **Tanım:** Spring'e bu sınıfın, Spring Bean tanımları içeren bir konfigürasyon sınıfı olduğunu belirtir. Genellikle `@Bean` anotasyonlu metotları içerir. Spring, bu sınıfları tarar ve içindeki `@Bean` metotlarının döndürdüğü nesneleri IoC Container'a kaydeder.
   - **Kullanım Alanı:** Uygulamanın Bean'lerini ve diğer ayarlarını Java kodu ile yapılandırmak için.
   - **Örnek:** (Yukarıdaki `AppConfig` örneğinde gösterildi.)

------



### II. Spring Boot Özgül Anotasyonları



Spring Boot, Spring Framework'ü hızla uygulama geliştirmek için basitleştiren bir katmandır.

1. **`@SpringBootApplication`**

   - **Tanım:** Bir Spring Boot uygulamasının ana başlangıç sınıfını işaretleyen bir kolaylık (convenience) anotasyonudur. Aslında üç temel anotasyonun birleşimidir:

     - **`@Configuration`**: Sınıfın bir konfigürasyon sınıfı olduğunu belirtir.
     - **`@EnableAutoConfiguration`**: Spring Boot'un classpath'teki (uygulamanın çalışmak için ihtiyaç duyduğu tüm kütüphanelerin ve sınıfların bulunduğu konum) bağımlılıklara, diğer Bean'lere ve özellik ayarlarına bakarak otomatik olarak uygulama ayarlarını yapmasını sağlar (örn. `spring-web` bağımlılığı varsa otomatik olarak gömülü Tomcat sunucusunu yapılandırır).
     - **`@ComponentScan`**: Spring'in, `@SpringBootApplication` sınıfının bulunduğu paketi ve altındaki tüm paketleri tarayarak `@Component`, `@Service`, `@Repository`, `@Controller` vb. anotasyonlu sınıfları bulmasını ve Bean olarak kaydetmesini sağlar.

   - **Kullanım Alanı:** Her Spring Boot uygulamasının ana sınıfı için kullanılır.

   - **Örnek:**

     Java

     ```java
     package com.example.demo;
     
     import org.springframework.boot.SpringApplication;
     import org.springframework.boot.autoconfigure.SpringBootApplication;
     
     @SpringBootApplication // Bu tek anotasyon, tüm gerekli Spring Boot ayarlarını etkinleştirir.
     public class DemoApplication {
         public static void main(String[] args) {
             SpringApplication.run(DemoApplication.class, args);
         }
     }
     ```

------



### III. Spring Web (REST API) Anotasyonları



RESTful web servisleri oluşturmak için kullanılan anotasyonlardır.

1. **`@RestController`**

   - **Tanım:** Bir sınıfın RESTful web servislerini sağlayan bir denetleyici (controller) olduğunu belirtir. Aslında `@Controller` ve `@ResponseBody` anotasyonlarının birleşimidir. `@Controller` gelen web isteklerini işlemek için kullanılırken, `@ResponseBody` metodun dönüş değerini HTTP yanıtının doğrudan gövdesine (body) yazmasını (örn. JSON veya XML olarak) sağlar.

   - **Kullanım Alanı:** REST API endpoint'lerini tanımlayan sınıflar için.

   - **Örnek:**

     Java

     ```java
     package com.example.demo.controller;
     
     import org.springframework.web.bind.annotation.RestController;
     import org.springframework.web.bind.annotation.GetMapping;
     
     @RestController // Bu sınıf, REST API endpoint'lerini sağlar.
     public class HelloController {
         @GetMapping("/hello") // GET isteğini /hello yoluna eşler
         public String sayHello() {
             return "Hello from Spring Boot!"; // Bu string doğrudan HTTP yanıt gövdesine yazılır.
         }
     }
     ```

2. **`@RequestMapping`**

   - **Tanım:** Bir metodu veya bir sınıfı belirli bir URL (URI - Uniform Resource Identifier) yoluna eşlemek için kullanılır. Hem GET, POST, PUT, DELETE gibi HTTP metotlarını hem de farklı parametreleri, header'ları ve medya tiplerini belirterek eşleme yapabilir.

   - **Kullanım Alanı:** Daha genel veya karmaşık HTTP istek eşlemeleri için. Genellikle `@GetMapping`, `@PostMapping` gibi daha spesifik anotasyonlar tercih edilir.

   - **Örnek:**

     Java

     ```java
     @RestController
     @RequestMapping("/api/products") // Bu sınıf altındaki tüm metotların yolu /api/products ile başlar
     public class ProductController {
     
         @GetMapping // GET /api/products isteğine yanıt verir
         public String getAllProducts() {
             return "Getting all products";
         }
     
         @RequestMapping(value = "/{id}", method = RequestMethod.PUT) // PUT /api/products/{id} isteğine yanıt verir
         public String updateProduct(@PathVariable Long id) {
             return "Updating product with ID: " + id;
         }
     }
     ```

3. **`@GetMapping`**

   - **Tanım:** `@RequestMapping(method = RequestMethod.GET)`'in kısaltmasıdır. Sadece HTTP GET isteklerini belirli bir URL yoluna eşlemek için kullanılır.

   - **Kullanım Alanı:** Bir kaynaktan veri okuma veya alma işlemleri için.

   - **Örnek:**

     Java

     ```java
     @RestController
     public class ProductController {
         @GetMapping("/products/{id}") // GET /products/123 gibi isteklere yanıt verir
         public String getProductById(@PathVariable Long id) {
             return "Product details for ID: " + id;
         }
     }
     ```

4. **`@PostMapping`**

   - **Tanım:** `@RequestMapping(method = RequestMethod.POST)`'in kısaltmasıdır. Sadece HTTP POST isteklerini belirli bir URL yoluna eşlemek için kullanılır.

   - **Kullanım Alanı:** Yeni bir kaynak oluşturma veya sunucuya veri gönderme işlemleri için.

   - **Örnek:**

     Java

     ```java
     @RestController
     public class ProductController {
         @PostMapping("/products") // POST /products isteğine yanıt verir (yeni ürün oluşturmak için)
         public String createProduct(@RequestBody String productData) { // Request body'deki veriyi alır
             return "Creating product with data: " + productData;
         }
     }
     ```

5. **`@PutMapping`**

   - **Tanım:** `@RequestMapping(method = RequestMethod.PUT)`'in kısaltmasıdır. Sadece HTTP PUT isteklerini belirli bir URL yoluna eşlemek için kullanılır.

   - **Kullanım Alanı:** Mevcut bir kaynağı tamamen güncellemek veya varsa güncellemek, yoksa oluşturmak (upsert) için.

   - **Örnek:**

     Java

     ```java
     @RestController
     public class ProductController {
         @PutMapping("/products/{id}") // PUT /products/123 isteğine yanıt verir (ürünü güncellemek için)
         public String updateProduct(@PathVariable Long id, @RequestBody String productData) {
             return "Updating product ID " + id + " with data: " + productData;
         }
     }
     ```

6. **`@DeleteMapping`**

   - **Tanım:** `@RequestMapping(method = RequestMethod.DELETE)`'in kısaltmasıdır. Sadece HTTP DELETE isteklerini belirli bir URL yoluna eşlemek için kullanılır.

   - **Kullanım Alanı:** Bir kaynağı silme işlemi için.

   - **Örnek:**

     Java

     ```java
     @RestController
     public class ProductController {
         @DeleteMapping("/products/{id}") // DELETE /products/123 isteğine yanıt verir (ürünü silmek için)
         public String deleteProduct(@PathVariable Long id) {
             return "Deleting product with ID: " + id;
         }
     }
     ```

7. **`@PatchMapping`**

   - **Tanım:** `@RequestMapping(method = RequestMethod.PATCH)`'in kısaltmasıdır. Sadece HTTP PATCH isteklerini belirli bir URL yoluna eşlemek için kullanılır.

   - **Kullanım Alanı:** Mevcut bir kaynağın **kısmi** olarak güncellenmesi için (örneğin sadece bir ürünün fiyatını güncellemek gibi).

   - **Örnek:**

     Java

     ```java
     @RestController
     public class ProductController {
         @PatchMapping("/products/{id}") // PATCH /products/123 isteğine yanıt verir (ürünü kısmi güncellemek için)
         public String patchProduct(@PathVariable Long id, @RequestBody String partialData) {
             return "Partially updating product ID " + id + " with data: " + partialData;
         }
     }
     ```

8. **`@PathVariable`**

   - **Tanım:** Bir metot parametresinin, URL şablonundaki (URI Template - URL içinde `{}` ile belirtilen değişken kısımlar) bir değişkenin değeriyle doldurulacağını belirtir.
   - **Kullanım Alanı:** Kaynak kimliklerini URL'nin bir parçası olarak almak için.
   - **Örnek:** (Yukarıdaki `@GetMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping` örneklerinde gösterildi.)

9. **`@RequestBody`**

   - **Tanım:** Bir metot parametresinin, HTTP isteğinin gövdesindeki (Request Body - HTTP isteğinin veri taşıyan ana içeriği) verilerle (genellikle JSON veya XML formatında) doldurulacağını belirtir. Spring, bu veriyi otomatik olarak Java nesnesine dönüştürür (deserialization - veriyi bir formatından diğerine dönüştürme, örneğin JSON'dan Java objesine).
   - **Kullanım Alanı:** POST, PUT veya PATCH istekleriyle gönderilen veri yüklerini almak için.
   - **Örnek:** (Yukarıdaki `@PostMapping`, `@PutMapping`, `@PatchMapping` örneklerinde gösterildi.)

10. **`@RequestParam`**

    - **Tanım:** Bir metot parametresinin, HTTP isteğindeki URL sorgu parametreleriyle (Query Parameters - URL'nin `?` işaretinden sonra gelen `key=value` çiftleri) doldurulacağını belirtir.

    - **Kullanım Alanı:** Filtreleme, sayfalama, sıralama gibi isteğe bağlı veya ek parametreler almak için.

    - **Örnek:**

      Java

      ```java
      @RestController
      public class ProductController {
          @GetMapping("/products/search") // GET /products/search?name=laptop&category=electronics
          public String searchProducts(
              @RequestParam String name, // 'name' sorgu parametresi
              @RequestParam(required = false) String category // 'category' opsiyonel bir sorgu parametresi
          ) {
              return "Searching products by name: " + name + " and category: " + category;
          }
      }
      ```

------



### IV. Spring Data JPA (Veri Erişim Katmanı) Anotasyonları



Veritabanı ile etkileşim kurarken kullanılan, JPA (Java Persistence API - Java uygulamalarında ilişkisel veritabanı işlemlerini standartlaştıran bir API) standardından gelen ve Spring Data JPA'nın sunduğu anotasyonlardır.

1. **`@Entity`**

   - **Tanım:** Bir Java sınıfının, veritabanındaki bir tabloya eşlenecek bir JPA varlığı (Entity - Veritabanındaki bir tablo satırını veya bir kavramı temsil eden Java nesnesi) olduğunu belirtir. JPA sağlayıcısı (örn. Hibernate), bu anotasyona sahip sınıfları tarar ve veritabanı tablolarıyla eşleşmelerini yapar.

   - **Kullanım Alanı:** Veritabanı tablolarının Java modellerini oluşturmak için.

   - **Örnek:**

     Java

     ```java
     package com.example.demo.model;
     
     import jakarta.persistence.Entity; // JPA'dan gelen import
     import jakarta.persistence.Id;
     import jakarta.persistence.GeneratedValue;
     import jakarta.persistence.GenerationType;
     
     @Entity // Bu sınıf bir veritabanı tablosuna eşlenir (varsayılan olarak sınıf adının küçük harfle hali)
     public class Product {
         @Id // Bu alan birincil anahtardır (Primary Key)
         @GeneratedValue(strategy = GenerationType.IDENTITY) // ID'nin veritabanı tarafından otomatik artırılmasını sağlar
         private Long id;
         private String name;
         private double price;
     
         // Constructors, getters, setters... (Lombok ile otomatik oluşturulabilir)
         public Product() {}
         public Product(Long id, String name, double price) {
             this.id = id;
             this.name = name;
             this.price = price;
         }
         // Getter ve Setter'lar buraya gelir...
         public Long getId() { return id; }
         public void setId(Long id) { this.id = id; }
         public String getName() { return name; }
         public void setName(String name) { this.name = name; }
         public double getPrice() { return price; }
         public void setPrice(double price) { this.price = price; }
     }
     ```

2. **`@Id`**

   - **Tanım:** Bir `@Entity` sınıfındaki bir alanın, veritabanı tablosunun birincil anahtarı (Primary Key - Bir tablodaki her bir satırı benzersiz olarak tanımlayan sütun veya sütun kümesi) olduğunu belirtir.
   - **Kullanım Alanı:** Her Entity sınıfında birincil anahtarı belirlemek için.
   - **Örnek:** (Yukarıdaki `Product` Entity örneğinde gösterildi.)

3. **`@GeneratedValue`**

   - **Tanım:** Birincil anahtarın (`@Id` ile işaretlenmiş alan) nasıl oluşturulacağını belirtir. `strategy` (strateji) özelliği ile farklı nesil stratejileri (örn. `GenerationType.IDENTITY`, `GenerationType.SEQUENCE`, `GenerationType.AUTO`) belirlenir.
   - **Kullanım Alanı:** Veritabanında otomatik olarak oluşturulan birincil anahtarlar için.
   - **Örnek:** (Yukarıdaki `Product` Entity örneğinde gösterildi.)

4. **`@Column`**

   - **Tanım:** Bir Entity alanının veritabanı tablosundaki bir sütunla nasıl eşleştiğini özelleştirmek için kullanılır. Sütun adı, null kabul edip etmemesi (nullable), uzunluk (length) gibi özellikleri belirtir.

   - **Kullanım Alanı:** Java alan adının veritabanı sütun adından farklı olduğu veya sütun özelliklerinin (örn. `nullable = false`) belirtilmesi gerektiği durumlarda.

   - **Örnek:**

     Java

     ```java
     import jakarta.persistence.Column;
     // ...
     @Entity
     public class Product {
         // ...
         @Column(name = "product_name", nullable = false, length = 100)
         private String name;
     
         @Column(precision = 10, scale = 2) // Toplam 10 basamak, virgülden sonra 2 basamak
         private double price;
         // ...
     }
     ```

5. **`@Transactional`**

   - **Tanım:** Bir metodun veya sınıfın, veritabanı işlemlerini (transaction - bir veya daha fazla veritabanı işleminin atomik olarak, yani ya tamamen başarılı olup sonuçların kaydedilmesi ya da tamamen başarısız olup hiçbir değişikliğin kaydedilmemesi prensibiyle gerçekleştirilmesi) yöneten bir işlem bağlamında çalışması gerektiğini belirtir. İşlem başarısız olursa (örn. bir hata fırlatılırsa) tüm değişiklikler geri alınır (rollback), başarılı olursa kaydedilir (commit).

   - **Kullanım Alanı:** Veritabanına yazma (CREATE, UPDATE, DELETE) işlemleri içeren servis metotlarında.

   - **Örnek:**

     Java

     ```java
     package com.example.demo.service;
     
     import org.springframework.stereotype.Service;
     import org.springframework.transaction.annotation.Transactional; // Gerekli import
     import com.example.demo.repository.ProductRepository;
     import com.example.demo.model.Product;
     
     @Service
     public class ProductService {
     
         private final ProductRepository productRepository;
     
         public ProductService(ProductRepository productRepository) {
             this.productRepository = productRepository;
         }
     
         @Transactional // Bu metodun tamamı bir veritabanı işlemi içinde çalışır.
         public Product saveNewProduct(Product product) {
             // Veritabanı yazma işlemleri burada yapılır
             // Eğer burada bir hata olursa, saveProduct çağrısı da geri alınır
             return productRepository.save(product); // ProductRepository'de bir save metodu olduğunu varsayalım
         }
     
         @Transactional(readOnly = true) // Sadece okuma işlemleri için optimize eder
         public Optional<Product> getProductById(Long id) {
             return productRepository.findById(id);
         }
     }
     ```