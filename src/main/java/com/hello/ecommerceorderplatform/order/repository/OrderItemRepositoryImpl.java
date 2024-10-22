package com.hello.ecommerceorderplatform.order.repository;

import com.hello.ecommerceorderplatform.order.dto.OrderItemResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.hello.ecommerceorderplatform.order.domain.QOrderItem.orderItem;

@Slf4j
@Repository
public class OrderItemRepositoryImpl {

    private final JPAQueryFactory factory;

    public OrderItemRepositoryImpl(EntityManager entityManager) {
        this.factory = new JPAQueryFactory(entityManager);
    }

    public List<OrderItemResponseDto> getOrder(Long orderId) {


        return factory.select(orderItem)
                .from(orderItem)
                .where(orderItem.order.id.eq(orderId))
                .fetch()
                .stream()
                .map(item -> new OrderItemResponseDto(item.getTotalPrice(), item.getItem()
                        .getItemName(), item.getOrderCount()))
                .collect(Collectors.toList());

    }
}
