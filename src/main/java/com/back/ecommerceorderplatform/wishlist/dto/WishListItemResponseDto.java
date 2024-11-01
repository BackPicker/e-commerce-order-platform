package com.back.ecommerceorderplatform.wishlist.dto;

import com.back.ecommerceorderplatform.wishlist.domain.WishListItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WishListItemResponseDto {

    @NotNull
    private Long    id;
    @NotNull
    private Long    productId;
    @NotBlank
    @Size(min = 1)
    private String  title;
    @NotNull
    private String  price;
    @NotNull
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
