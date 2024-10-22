package com.hello.ecommerceorderplatform.delivery.repository;

import com.hello.ecommerceorderplatform.delivery.domain.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
