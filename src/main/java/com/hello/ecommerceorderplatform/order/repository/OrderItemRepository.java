package com.hello.ecommerceorderplatform.order.repository;

import com.hello.ecommerceorderplatform.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
