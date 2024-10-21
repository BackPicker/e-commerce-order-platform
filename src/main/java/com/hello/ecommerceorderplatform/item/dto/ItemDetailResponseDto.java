package com.hello.ecommerceorderplatform.item.dto;


import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemDetailResponseDto {

    private String itemName;
    private String category;
    private int    price;
    private int    quantity;
    private String description;

    @QueryProjection
    public ItemDetailResponseDto(String itemName, String category, int price, int quantity, String description) {
        this.itemName    = itemName;
        this.category    = category;
        this.price       = price;
        this.quantity    = quantity;
        this.description = description;
    }
}