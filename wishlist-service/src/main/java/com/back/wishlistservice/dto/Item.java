package com.back.wishlistservice.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {
    private Long   itemId;
    private String itemName;
    private String category;
    private int    price;
    private int    quantity;
}
