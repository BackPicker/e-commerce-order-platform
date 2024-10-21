package com.hello.ecommerceorderplatform.item.dto;

import com.querydsl.core.annotations.QueryProjection;

public record ItemResponseDto(String itemName, String category, int price, int quantity) {

    @QueryProjection
    public ItemResponseDto {
    }
}
