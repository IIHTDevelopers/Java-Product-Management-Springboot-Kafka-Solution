package com.assess.kafka.consumer.jpa;

import com.assess.kafka.consumer.entity.ProductEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductEventRepository extends JpaRepository<ProductEvent, Long> {
    // Define custom query methods if needed
}