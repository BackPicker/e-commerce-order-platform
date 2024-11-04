package com.back.wishlistservice.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {
    private Long    itemId;
    private String  itemName;
    private String  category;
    private Integer price;
    private Integer quantity;
}
