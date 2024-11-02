package com.back.itemservice.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemResponseDto {

    private String itemName;
    private String category;
    private int    price;
    private int    quantity;

    @QueryProjection
    public ItemResponseDto(String itemName,
                           String category,
                           int price,
                           int quantity) {
        this.itemName = itemName;
        this.category = category;
        this.price    = price;
        this.quantity = quantity;
    }
}