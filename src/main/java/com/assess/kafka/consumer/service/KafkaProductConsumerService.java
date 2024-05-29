package com.assess.kafka.consumer.service;


import com.assess.kafka.consumer.entity.Product;
import com.assess.kafka.consumer.jpa.ProductRepository;
import com.assess.kafka.producer.domain.ProductDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class KafkaProductConsumerService {
    @Autowired
    private ProductRepository productRepository;

    public Product listenCreateProduct(ProductDto productDto) {
        Product dbProduct = Product.builder()
                .customerId(productDto.getCustomerId())
                .status(productDto.getStatus())
                .totalPrice(productDto.getTotalPrice())
                .productDate(LocalDateTime.now())
                .build();
        return productRepository.save(dbProduct);
    }

}
