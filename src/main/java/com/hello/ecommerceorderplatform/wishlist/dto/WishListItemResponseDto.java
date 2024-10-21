package com.hello.ecommerceorderplatform.wishlist.dto;

import com.hello.ecommerceorderplatform.wishlist.domain.WishListItem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WishListItemResponseDto {
    private Long    id;
    private Long    productId;
    private String  title;
    private String  price;
    private Integer quantity;

    public WishListItemResponseDto(WishListItem wishListItem) {
        this.id        = wishListItem.getId();
        this.productId = wishListItem.getItem()
                .getId();
        this.title     = wishListItem.getItem()
                .getItemName();
        this.price     = String.valueOf(wishListItem.getItem()
                .getPrice());
        this.quantity  = wishListItem.getWishListItemQuantity();
    }
}
