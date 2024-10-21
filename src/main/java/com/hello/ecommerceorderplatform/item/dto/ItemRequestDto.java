package com.hello.ecommerceorderplatform.item.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
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

}
