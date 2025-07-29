package com.argelaa.fstapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
    REST API'nin dışarıya açılan kapısıdır. Gelen HTTP isteklerini karşılar, iş mantığını
    tetikler ve HTTP yanıtların döndürür. Spring MVC'nin @Controller notasyonuna ek olarak,
    @RestController notasyonu, otomatik olarak tüm metotların @ResponseBody notasyonuna 
    sahip olmasını sağlar. Bu da, metotların geri dönüş değerlerini doğrudan HTTP yanıtının
    gövdesine yazılacağı anlamına gelir.
*/
@RestController /* Bu sınıfın bir REST API olduğunu belirtir. */
@RequestMapping("/api") /* Bu servise "/api/hello" adresinden ulaşılabilir. */
public class HelloController 
{
    @GetMapping("/hello") /* "/api/hello" adresine bir GET isteği gelirse bu metodu çalıştır. */
    public String sayHello() 
    {
        return "Merhaba Spring Boot REST API (Sublime Text'ten)!";
    }

    @GetMapping("/howareyou")
    public String good()
    {
        return "I'm fine, you?";
    }
}