package com.hello.ecommerceorderplatform.wishlist.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class WishListItemDto {
    private Integer quantity;
    private Long    itemId; // 아이템 ID

    public WishListItemDto(Integer quantity, Long itemId) {
        this.quantity = quantity;
        this.itemId   = itemId;
    }

}
