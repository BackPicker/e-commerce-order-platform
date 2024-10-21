package com.hello.ecommerceorderplatform.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ItemRequestDto {

    @NotBlank
    private String itemName;
    @NotBlank
    private String category;
    @NotBlank
    private int    price;
    @NotBlank
    private int    quantity;
    @NotBlank
    private String description;


}
