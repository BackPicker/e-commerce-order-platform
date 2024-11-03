package com.back.orderservice.order.dto;

import com.back.orderservice.order.domain.OrderItem;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemResponseDTO {
    private Long    id;           // 주문 항목 ID
    private Long    itemId;      // 제품 ID
    private Integer orderCount; // 주문 수량

    @Builder
    public OrderItemResponseDTO(Long id,
                                Long itemId,
                                Integer orderCount) {
        this.id         = id;
        this.itemId     = itemId;
        this.orderCount = orderCount;
    }

    public static OrderItemResponseDTO entityToDTO(OrderItem orderItem,
                                                   Item item) {
        return OrderItemResponseDTO.builder()
                .id(orderItem.getId())
                .itemId(item.getItemId())
                .orderCount(orderItem.getOrderCount())
                .build();
    }
}
