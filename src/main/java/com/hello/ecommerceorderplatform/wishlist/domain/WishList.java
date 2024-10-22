package com.hello.ecommerceorderplatform.wishlist.domain;

import com.hello.ecommerceorderplatform.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class WishList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 위시리스트 Id

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 위시리스트 회원

    @OneToMany(mappedBy = "wishList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WishListItem> wishListItemList = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt; // 위시리스트 생성일

    public WishList(User user) {
        this.user      = user;
        this.createdAt = LocalDateTime.now();
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
