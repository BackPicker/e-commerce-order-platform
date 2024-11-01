package com.back.ecommerceorderplatform.order.repository;

import com.back.ecommerceorderplatform.order.domain.Order;
import com.back.ecommerceorderplatform.order.domain.OrderItem;
import com.back.ecommerceorderplatform.order.dto.OrderItemResponseDto;
import com.back.ecommerceorderplatform.order.dto.OrderResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.back.ecommerceorderplatform.order.domain.QOrder.order;


@Repository
public class OrderRepositoryImpl {

    private final JPAQueryFactory factory;

    public OrderRepositoryImpl(EntityManager entityManager) {
        this.factory = new JPAQueryFactory(entityManager);
    }

    public List<OrderResponseDto> findByUserIdOrderByCreateDateDesc(Long userId) {
        List<Order> orders = factory.select(order)
                .from(order)
                .where(order.user.id.eq(userId))
                .fetch();

        return orders.stream()
                .map(order -> new OrderResponseDto(order.getUser()
                        .getUsername(), order.getTotalOrderPrice(), convertToOrderItemResponseDto(order.getOrderItems()),
                        order.getOrderStatus()))
                .collect(Collectors.toList());
    }

    public List<OrderItemResponseDto> convertToOrderItemResponseDto(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> new OrderItemResponseDto(item.getTotalPrice(), item.getItem()
                        .getItemName(), item.getOrderCount()))
                .collect(Collectors.toList());
    }


}
