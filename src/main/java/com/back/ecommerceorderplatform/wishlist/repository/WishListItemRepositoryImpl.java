package com.back.ecommerceorderplatform.wishlist.repository;

import com.back.ecommerceorderplatform.wishlist.domain.WishList;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.back.ecommerceorderplatform.wishlist.domain.QWishList.wishList;

@Slf4j
@Repository
public class WishListItemRepositoryImpl {


    private final JPAQueryFactory factory;

    public WishListItemRepositoryImpl(EntityManager entityManager) {
        this.factory = new JPAQueryFactory(entityManager);
    }


    public Optional<WishList> findByUserId(Long id) {
        return Optional.ofNullable(factory.selectFrom(wishList)
                .leftJoin(wishList.wishListItemList) // Join WishListItem
                .fetchJoin()
                .where(wishList.user.id.eq(id))
                .fetchOne());
    }
}