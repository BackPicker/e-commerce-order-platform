package com.back.itemservice.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WishList {

    private Long    id;
    private Long    userId;
    private Long    itemId;
    private Integer wishListItemQuantity;

}
