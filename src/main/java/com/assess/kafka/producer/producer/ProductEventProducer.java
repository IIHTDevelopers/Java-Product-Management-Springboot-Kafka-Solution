package com.assess.kafka.producer.producer;

import com.assess.kafka.producer.domain.ProductDto;
import com.assess.kafka.producer.domain.ProductEvent;
import com.assess.kafka.producer.domain.ProductEventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class ProductEventProducer {

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    @Value("${spring.kafka.product.topic.create-product}")
    private String topic;

    @Autowired
    public ProductEventProducer(KafkaTemplate<String, ProductEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public ProductEvent sendCreateProductEvent(ProductDto productDto, String eventDetails) {
        ProductEvent productEvent = ProductEvent.
                builder()
                .productDto(productDto)
                .eventType(ProductEventType.PRODUCT_ALLOTED)
                .eventDetails(eventDetails + productDto.getCustomerId())
                .build();
        try {
            CompletableFuture<SendResult<String, ProductEvent>> sendResultCompletableFuture = kafkaTemplate.send(topic, productEvent.getEventType().toString(), productEvent);
           return sendResultCompletableFuture.get().getProducerRecord().value();
        } catch (Exception e) {
            log.debug("Error occurred while publishing message due to " + e.getMessage());
        }
        return productEvent;
    }
}
