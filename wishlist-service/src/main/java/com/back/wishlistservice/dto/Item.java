package com.back.wishlistservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Item {
    private Long   id;
    private String itemName;
    private String  category;
    private Integer price;
    private Integer quantity;
}
