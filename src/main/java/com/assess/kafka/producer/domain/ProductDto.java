package com.assess.kafka.producer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ProductDto {
    private String productId;
    private Long customerId;
    private LocalDateTime productDate;
    private BigDecimal totalPrice;
    private String status;

    public ProductDto() {
    }
}
