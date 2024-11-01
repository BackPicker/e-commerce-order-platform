package com.back.ecommerceorderplatform.order.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemResponseDto {

    private int    totalPrice; // 총 가격
    private String itemName; // 아이템 이름
    private int    orderCount;  // 주문 수량

    @QueryProjection
    public OrderItemResponseDto(int totalPrice,
                                String itemName,
                                int orderCount) {
        this.totalPrice = totalPrice;
        this.itemName   = itemName;
        this.orderCount = orderCount;
    }

}
