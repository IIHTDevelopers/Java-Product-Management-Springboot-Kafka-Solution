package com.assess.kafka.consumer.consumer;

import com.assess.kafka.consumer.entity.Product;
import com.assess.kafka.consumer.service.KafkaProductConsumerService;
import com.assess.kafka.consumer.service.ProductEventsService;
import com.assess.kafka.producer.domain.ProductEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductEventsConsumerManualOffset implements AcknowledgingMessageListener<String, ProductEvent> {
    @Autowired
    private KafkaProductConsumerService productConsumerService;
    @Autowired
    private ProductEventsService productEventsService;

    @Override
    @KafkaListener(topics = "${spring.kafka.product.topic.create-product}", groupId = "${spring.kafka.product.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onMessage(ConsumerRecord<String, ProductEvent> consumerRecord, Acknowledgment acknowledgment) {
        log.info("ProductEventsConsumerManualOffset Received message from Kafka: " + consumerRecord.value());
            if (consumerRecord.offset() % 2 == 0) {
                throw new RuntimeException("This is really odd.");
            }
            processMessage(consumerRecord.value());
            acknowledgment.acknowledge();
    }

    private void processMessage(ProductEvent productEvent) {
        Product dbProduct = productConsumerService.listenCreateProduct(productEvent.getProductDto());
        productEventsService.listenCreateProductEvent(productEvent, dbProduct);
    }

}