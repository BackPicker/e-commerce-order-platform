package com.back.ecommerceorderplatform.wishlist.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WishListItemDeleteRequestDto {
    private Long itemId;
}
