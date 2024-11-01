package com.back.ecommerceorderplatform.order.repository;

import com.back.ecommerceorderplatform.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
