package com.assess.kafka.producer.controller;

import com.assess.kafka.producer.domain.ProductDto;
import com.assess.kafka.producer.domain.ProductEvent;
import com.assess.kafka.producer.producer.ProductEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductEventProducer productEventProducer;

    @PostMapping("/")
    public ResponseEntity<?> createProduct(@RequestBody ProductDto requestedProductDto) {
        try {
            ProductEvent productEvent = productEventProducer.sendCreateProductEvent(requestedProductDto, "Product Created");
            return ResponseEntity.status(HttpStatus.CREATED).body(productEvent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending Product event");
        }
    }
}
