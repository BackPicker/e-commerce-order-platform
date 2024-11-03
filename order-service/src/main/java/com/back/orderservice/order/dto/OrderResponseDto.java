package com.back.orderservice.order.dto;

import com.back.orderservice.order.domain.Order;
import com.back.orderservice.order.domain.OrderStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResponseDto {
    private Long                       orderId;                      // 주문 ID
    private Long                       userId;                       // 사용자 ID
    private int                        totalOrderPrice;               // 총 주문 가격
    private OrderStatus                orderStatus;           // 주문 상태
    private List<OrderItemResponseDTO> items;  // 주문 항목 리스트

    @Builder
    public OrderResponseDto(Long orderId,
                            Long userId,
                            int totalOrderPrice,
                            OrderStatus orderStatus,
                            List<OrderItemResponseDTO> items) {
        this.orderId         = orderId;
        this.userId          = userId;
        this.totalOrderPrice = totalOrderPrice;
        this.orderStatus     = orderStatus;
        this.items           = items;
    }

    public static OrderResponseDto entityToDto(Order order,
                                               List<OrderItemResponseDTO> orderItemDtos) {
        return OrderResponseDto.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .totalOrderPrice(order.getTotalOrderPrice())
                .orderStatus(order.getOrderStatus())
                .items(orderItemDtos)
                .build();
    }
}
