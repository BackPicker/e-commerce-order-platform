package com.hello.ecommerceorderplatform.wishlist.repository;

import com.hello.ecommerceorderplatform.wishlist.domain.QWishList;
import com.hello.ecommerceorderplatform.wishlist.domain.WishList;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
public class WishListItemRepositoryImpl {


    private final JPAQueryFactory factory;

    public WishListItemRepositoryImpl(EntityManager entityManager) {
        this.factory = new JPAQueryFactory(entityManager);
    }


    public Optional<WishList> findByUserId(Long id) {
        return Optional.ofNullable(factory.selectFrom(QWishList.wishList)
                .where(QWishList.wishList.user.id.eq(id))
                .fetchOne());
    }
}