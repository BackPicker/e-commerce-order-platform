package com.back.ecommerceorderplatform.order.dto;

import com.back.ecommerceorderplatform.order.domain.OrderStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 주문 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResponseDto {

    private String                     username;
    private long                       totalOrderPrice;
    private List<OrderItemResponseDto> orderItems;
    private OrderStatus                orderStatus;

    @QueryProjection
    public OrderResponseDto(String username,
                            long totalOrderPrice,
                            List<OrderItemResponseDto> orderItems,
                            OrderStatus orderStatus) {
        this.username        = username;
        this.totalOrderPrice = totalOrderPrice;
        this.orderItems      = orderItems;
        this.orderStatus     = orderStatus;
    }


}
