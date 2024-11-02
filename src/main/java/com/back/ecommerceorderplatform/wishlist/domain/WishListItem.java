package com.back.ecommerceorderplatform.wishlist.domain;

import com.back.itemservice.domain.Item;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@ToString(exclude = {"wishList"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WishListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private Integer wishListItemQuantity; // 위시리스트 수량

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "item_id")
    private Item item; // 아이템

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wish_list_id")
    private WishList wishList; // ManyToOne 관계

    public WishListItem(Integer wishListItemQuantity,
                        Item item) {
        this.wishListItemQuantity = wishListItemQuantity;
        this.item                 = item;
    }

    public int totalWishListPrice(Item item,
                                  Integer wishListItemQuantity) {
        return item.getPrice() * wishListItemQuantity;
    }


}