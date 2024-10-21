package com.hello.ecommerceorderplatform.item.dto;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ItemListResponseDto {
    private final String itemName;
    private final String category;
    private final int    price;
    private final int    quantity;
    private final String description; // 상품 설명 추가

    @QueryProjection
    public ItemListResponseDto(String itemName, String category, int price, int quantity, String description) {
        this.itemName    = itemName;
        this.category    = category;
        this.price       = price;
        this.quantity    = quantity;
        this.description = description;
    }
}