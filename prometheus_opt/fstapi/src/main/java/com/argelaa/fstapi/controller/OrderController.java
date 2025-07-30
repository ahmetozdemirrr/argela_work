package com.argelaa.fstapi.controller;

import com.argelaa.fstapi.model.Product;
import com.argelaa.fstapi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController
{
    private final ProductService productService;

    @Autowired
    public OrderController(ProductService productService)
    {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> placeOrder(@RequestParam Long productId,
                                              @RequestParam Long customerId,
                                              @RequestParam Long quantity) {
        return productService.purchaseProduct(productId, customerId, quantity)
                .map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }
}
