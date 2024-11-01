package com.back.ecommerceorderplatform.delivery.repository;

import com.back.ecommerceorderplatform.delivery.domain.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
