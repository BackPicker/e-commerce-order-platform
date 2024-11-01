package com.back.ecommerceorderplatform.order.repository;

import com.back.ecommerceorderplatform.order.domain.QOrderItem;
import com.back.ecommerceorderplatform.order.dto.OrderItemResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;


@Slf4j
@Repository
public class OrderItemRepositoryImpl {

    private final JPAQueryFactory factory;

    public OrderItemRepositoryImpl(EntityManager entityManager) {
        this.factory = new JPAQueryFactory(entityManager);
    }

    public List<OrderItemResponseDto> getOrderItemsByOrderId(Long orderId) {
        QOrderItem orderItem = QOrderItem.orderItem;
        List<OrderItemResponseDto> orderItems = factory.select(orderItem)
                .from(orderItem)
                .join(orderItem.item) // Fetch join
                .where(orderItem.order.id.eq(orderId)) // Where 조건 설정
                .fetch() // 쿼리 실행
                .stream()
                .map(item -> new OrderItemResponseDto(item.getTotalPrice(), item.getItem()
                        .getItemName(), item.getOrderCount()))
                .toList();

        if (orderItems.isEmpty()) {
            log.warn("해당 ID로 된 주문을 찾을 수 없습니다, ID : {}", orderId);
        }

        return orderItems;
    }
}