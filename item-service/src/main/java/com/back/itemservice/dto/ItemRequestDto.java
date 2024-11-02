package com.back.itemservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemRequestDto {

    @NotBlank
    private String itemName;
    @NotBlank
    private String category;
    @NotNull
    @Min(value = 0)
    private int    price;
    @NotNull
    @Min(value = 0)
    private int    quantity;

    public ItemRequestDto(String itemName,
                          String category,
                          int price,
                          int quantity) {
        this.itemName    = itemName;
        this.category    = category;
        this.price       = price;
        this.quantity    = quantity;
    }
}
