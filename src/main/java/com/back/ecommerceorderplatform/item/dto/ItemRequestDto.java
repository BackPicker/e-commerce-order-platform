package com.back.ecommerceorderplatform.item.dto;

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
    @NotBlank
    private String description;

    public ItemRequestDto(String itemName,
                          String category,
                          int price,
                          int quantity,
                          String description) {
        this.itemName    = itemName;
        this.category    = category;
        this.price       = price;
        this.quantity    = quantity;
        this.description = description;
    }
}
