package com.argelaa.fstapi.controller;

// Bu import'lar önemli, eksikse ekle
import com.argelaa.common.OrderPlacedEvent;
import com.argelaa.fstapi.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final KafkaProducerService producerService;

    @Autowired
    public OrderController(KafkaProducerService producerService) {
        this.producerService = producerService;
    }

    // EN ÖNEMLİ DEĞİŞİKLİK BURADA: @RequestBody ANNOTATION'I
    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody OrderPlacedEvent orderEvent) {
        try {
            producerService.sendOrderEvent(orderEvent);
            // Başarılı olursa 201 Created durum kodu dönecek
            return new ResponseEntity<>("Order placed successfully!", HttpStatus.CREATED);
        } catch (Exception e) {
            // Bir hata olursa 500 Internal Server Error dönecek
            return new ResponseEntity<>("Failed to place order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}