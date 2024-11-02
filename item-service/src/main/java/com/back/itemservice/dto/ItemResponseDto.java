package com.back.itemservice.dto;

import com.back.itemservice.domain.Item;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemResponseDto {

    private String itemName;
    private String category;
    private int    price;
    private int    quantity;

    @Builder
    public ItemResponseDto(Item item) {
        this.itemName = item.getItemName();
        this.category = item.getCategory();
        this.price    = item.getPrice();
        this.quantity = item.getQuantity();
    }
}