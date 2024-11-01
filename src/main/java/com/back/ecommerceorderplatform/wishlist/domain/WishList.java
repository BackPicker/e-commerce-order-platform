package com.back.ecommerceorderplatform.wishlist.domain;

import com.back.common.entity.BaseEntity;
import com.back.ecommerceorderplatform.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WishList extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 위시리스트 Id

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 위시리스트 회원

    @OneToMany(mappedBy = "wishList", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<WishListItem> wishListItemList = new ArrayList<>();


    public WishList(User user) {
        this.user = user;
    }


    public void addWishListItem(WishListItem wishListItem) {
        if (!wishListItemList.contains(wishListItem)) {
            wishListItemList.add(wishListItem);
            wishListItem.setWishList(this); // 연관관계 설정
        }
    }

    public void removeWishListItem(WishListItem wishListItem) {
        wishListItemList.remove(wishListItem);
        wishListItem.setWishList(null); // 연관관계 정리
    }
}
