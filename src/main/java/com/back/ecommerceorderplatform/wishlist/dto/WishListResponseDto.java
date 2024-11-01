package com.back.ecommerceorderplatform.wishlist.dto;

import com.back.ecommerceorderplatform.wishlist.domain.WishList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class WishListResponseDto {

    @NotBlank
    private String                        username;
    @NotNull
    private Long                          wishlistId;
    @NotNull
    @Positive
    private Long                          totalPrice;
    private List<WishListItemResponseDto> wishListItemList;

    public WishListResponseDto(String username,
                               WishList wishList,
                               Long totalPrice) {
        this.username         = username;
        this.wishlistId       = wishList.getId();
        this.totalPrice       = totalPrice;
        this.wishListItemList = wishList.getWishListItemList()
                .stream()
                .map(WishListItemResponseDto::new)
                .toList();
    }
}
