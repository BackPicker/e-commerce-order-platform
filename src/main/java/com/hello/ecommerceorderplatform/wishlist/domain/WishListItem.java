package com.hello.ecommerceorderplatform.wishlist.domain;

import com.hello.ecommerceorderplatform.item.domain.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WishListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer wishListItemQuantity;

    @JoinColumn(name = "item_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @JoinColumn(name = "wish_list_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private WishList wishList;

}
