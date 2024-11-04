package com.back.orderservice.order.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {
    private Long    itemId;
    private String  itemName;
    private String  category;
    private long    price;
    private Integer quantity;
}
