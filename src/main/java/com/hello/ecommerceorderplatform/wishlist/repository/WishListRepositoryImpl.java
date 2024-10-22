package com.hello.ecommerceorderplatform.wishlist.repository;

import com.hello.ecommerceorderplatform.wishlist.domain.WishList;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.hello.ecommerceorderplatform.wishlist.domain.QWishList.wishList;

@Slf4j
@Repository
public class WishListRepositoryImpl {

    private final JPAQueryFactory factory;

    public WishListRepositoryImpl(EntityManager entityManager) {
        this.factory = new JPAQueryFactory(entityManager);
    }

    public boolean existsByUserId(Long id) {
        return factory.selectFrom(wishList)
                .where(wishList.user.id.eq(id))
                .fetch()
                .isEmpty();
    }

    public Optional<WishList> findByUserId(Long userId) {
        return Optional.ofNullable(factory.selectFrom(wishList)
                .leftJoin(wishList.wishListItemList)
                .fetchJoin()
                .where(wishList.user.id.eq(userId))
                .fetchOne());
    }
}