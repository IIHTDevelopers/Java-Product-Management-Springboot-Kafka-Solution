package com.assess.kafka.producer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ProductEvent {
    private String eventId;
    private ProductEventType eventType;
    private ProductDto productDto;
    private String eventDetails;

    public ProductEvent() {

    }
}

