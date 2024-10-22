package com.hello.ecommerceorderplatform.order.repository;

import com.hello.ecommerceorderplatform.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
