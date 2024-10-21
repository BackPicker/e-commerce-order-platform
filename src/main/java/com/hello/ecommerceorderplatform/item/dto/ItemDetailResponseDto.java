package com.hello.ecommerceorderplatform.item.dto;


import com.querydsl.core.annotations.QueryProjection;


public record ItemDetailResponseDto(String itemName, String category, int price, int quantity, String description) {
    @QueryProjection
    public ItemDetailResponseDto {
    }
}