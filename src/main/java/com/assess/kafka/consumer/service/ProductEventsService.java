package com.assess.kafka.consumer.service;


import com.assess.kafka.consumer.entity.Product;
import com.assess.kafka.consumer.entity.ProductEvent;
import com.assess.kafka.consumer.entity.ProductEventType;
import com.assess.kafka.consumer.jpa.ProductEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.assess.kafka.consumer.entity.ProductEventType.PRODUCT_ALLOTED;
import static com.assess.kafka.consumer.entity.ProductEventType.PRODUCT_CANCELLED;

@Service
public class ProductEventsService {

    private final ProductEventRepository productEventRepository;

    @Autowired
    public ProductEventsService(ProductEventRepository productEventRepository) {
        this.productEventRepository = productEventRepository;
    }


    public void listenCreateProductEvent(com.assess.kafka.producer.domain.ProductEvent productEvent, Product dbProduct) {
        ProductEvent event = ProductEvent.builder()
                .eventType(mapEventType(productEvent.getEventType()))
                .eventDetails(productEvent.getEventDetails())
                .productId(dbProduct.getId())
                .build();
        productEventRepository.save(event);
    }

    private ProductEventType mapEventType(com.assess.kafka.producer.domain.ProductEventType eventType) {
        return switch (eventType) {
            case PRODUCT_ALLOTED -> PRODUCT_ALLOTED;
            case PRODUCT_CANCELLED -> PRODUCT_CANCELLED;
        };
    }
}
