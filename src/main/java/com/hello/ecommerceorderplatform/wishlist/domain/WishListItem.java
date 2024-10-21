package com.hello.ecommerceorderplatform.wishlist.domain;

import com.hello.ecommerceorderplatform.item.domain.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WishListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private Integer wishListItemQuantity; // 위시리스트 수량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item; // 아이템

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wish_list_id", nullable = false)
    private WishList wishList; // 위시리스트

    public WishListItem(Integer wishListItemQuantity, Item item) {
        this.wishListItemQuantity = wishListItemQuantity;
        this.item                 = item;
    }

    public int totalWishListPrice(Item item, Integer wishListItemQuantity) {
        if (item == null || wishListItemQuantity == null) {
            throw new IllegalArgumentException("아이템이나 수량이 null일 수 없습니다.");
        }
        return item.getPrice() * wishListItemQuantity;
    }


}