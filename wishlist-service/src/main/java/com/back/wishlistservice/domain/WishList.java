package com.back.wishlistservice.domain;

import com.back.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WishList extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long itemId;

    private Integer wishListItemQuantity; // 위시리스트 수량

    public WishList(Long userId,
                    Long itemId,
                    Integer wishListItemQuantity) {
        this.userId               = userId;
        this.itemId               = itemId;
        this.wishListItemQuantity = wishListItemQuantity;
    }

    public void updateQuantity(Integer sumQuantity) {
        this.wishListItemQuantity = sumQuantity;
    }
}
