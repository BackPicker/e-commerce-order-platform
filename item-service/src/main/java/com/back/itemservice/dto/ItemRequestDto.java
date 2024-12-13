package com.back.itemservice.dto;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemRequestDto {

    private String itemName;
    private String category;
    private int    price;
    private int    quantity;

    @Builder
    public ItemRequestDto(String itemName,
                          String category,
                          int price,
                          int quantity) {
        this.itemName = itemName;
        this.category = category;
        this.price    = price;
        this.quantity = quantity;
    }
}
