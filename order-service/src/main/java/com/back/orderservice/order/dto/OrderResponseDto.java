package com.back.orderservice.order.dto;

import com.back.orderservice.order.domain.Order;
import com.back.orderservice.order.domain.OrderStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResponseDto {

    private Long        orderId;
    private Long        userId;
    private String      itemName;
    private Long        totalOrderPrice;
    private OrderStatus orderStatus;

    @Builder
    public OrderResponseDto(Long orderId,
                            Long userId,
                            String itemName,
                            Long totalOrderPrice,
                            OrderStatus orderStatus) {
        this.orderId         = orderId;
        this.userId          = userId;
        this.itemName        = itemName;
        this.totalOrderPrice = totalOrderPrice;
        this.orderStatus     = orderStatus;
    }


    public static OrderResponseDto entityToDTO(Order order,
                                               Item item,
                                               Long userId) {

        return OrderResponseDto.builder()
                .orderId(order.getId())
                .userId(userId)
                .itemName(item.getItemName())
                .totalOrderPrice(order.getTotalOrderPrice())
                .orderStatus(order.getOrderStatus())
                .build();
    }
}
