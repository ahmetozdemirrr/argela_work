package com.argelaa.fstapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

/* 
	bu notasyonla işaretlenmiş sınıf genelde entry point (main fonksiyonu)
	bulunan yerdir.
	
	- @Configuration: Bu sınıfın Spring konteyneri tarafından bean 
	tanımlamaları için kullanılabileceğini belirtir.

	- @EnableAutoConfiguration: Spring Boot'un classpath bağımlılıklarına, 
	diğer bean'lere ve çeşitli ayarlara bakarak otomatik olarak yapılandırmalar 
	oluşturmasını sağlar. Örneğin, spring-boot-starter-web eklediğinizde otomatik 
	olarak Tomcat sunucusunu ve Spring MVC'yi yapılandırır.

	- @ComponentScan: Spring'in @Component, @Service, @Repository, @Controller 
	gibi notasyonlarla işaretlenmiş sınıfları bulup Spring konteynerine bean 
	olarak kaydetmesini sağlar. Varsayılan olarak, bu notasyonun bulunduğu paketi 
	ve alt paketlerini tarar.

	gibi componentleri birleştirip tek seferde hepsini halleder.
*/
@SpringBootApplication
@EnableRetry
public class FstapiApplication 
{
	public static void main(String[] args) 
	{
		SpringApplication.run(FstapiApplication.class, args);
	}
}

/*
	kafka *
	grafana

	promotheus, kafka mesaj gönderdiğinde scraping yapsın, belirli zaman aralıklarıyla değil.
 */