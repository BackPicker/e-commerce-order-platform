package com.back.itemservice.dto;

import lombok.Getter;

@Getter
public class ItemQuantityResponseDto {
    private Integer quantity;

    public ItemQuantityResponseDto(Integer quantity) {
        this.quantity = quantity;
    }


}
