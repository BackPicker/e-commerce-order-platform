package com.hello.ecommerceorderplatform.item.dto;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ItemDetailResponseDto {
    private String itemName;
    private String category;
    private int    price;
    private int    quantity;
    private String description; // 상품 설명 추가

    @QueryProjection
    public ItemDetailResponseDto(String itemName, String category, int price, int quantity, String description) {
        this.itemName    = itemName;
        this.category    = category;
        this.price       = price;
        this.quantity    = quantity;
        this.description = description;
    }
}