package com.hello.ecommerceorderplatform.wishlist.dto;

import com.hello.ecommerceorderplatform.wishlist.domain.WishList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class WishListResponseDto {

    private String                        username;
    private Long                          wishlistId;
    private Long                          totalPrice;
    private List<WishListItemResponseDto> wishListItemList;

    public WishListResponseDto(String username, WishList wishList, Long totalPrice) {
        this.username         = username;
        this.wishlistId       = wishList.getId();
        this.totalPrice       = totalPrice;
        this.wishListItemList = wishList.getWishListItemList()
                .stream()
                .map(WishListItemResponseDto::new)
                .toList();
    }
}
