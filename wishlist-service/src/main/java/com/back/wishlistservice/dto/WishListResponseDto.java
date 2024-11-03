package com.back.wishlistservice.dto;


import com.back.wishlistservice.domain.WishList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class WishListResponseDto {
    private Long    userId;
    private Long    itemId;
    private Integer quantity;

    public WishListResponseDto(WishList wishList) {
        this.userId   = wishList.getUserId();
        this.itemId   = wishList.getItemId();
        this.quantity = wishList.getWishListItemQuantity();
    }
}
