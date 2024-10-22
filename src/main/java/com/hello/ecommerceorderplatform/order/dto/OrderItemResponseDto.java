package com.hello.ecommerceorderplatform.order.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemResponseDto {

    private int    totalPrice;
    private String itemName;
    private int    orderCount;

    @QueryProjection
    public OrderItemResponseDto(int totalPrice, String itemName, int orderCount) {
        this.totalPrice = totalPrice;
        this.itemName   = itemName;
        this.orderCount = orderCount;
    }
}
