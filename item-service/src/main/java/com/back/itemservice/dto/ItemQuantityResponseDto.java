package com.back.itemservice.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemQuantityResponseDto {
    private Integer quantity;

    public ItemQuantityResponseDto(Integer quantity) {
        this.quantity = quantity;
    }


}
