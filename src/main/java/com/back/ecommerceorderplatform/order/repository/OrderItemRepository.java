package com.back.ecommerceorderplatform.order.repository;


import com.back.ecommerceorderplatform.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
