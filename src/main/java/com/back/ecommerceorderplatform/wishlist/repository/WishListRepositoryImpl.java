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

    public void deleteWishListItem(Long wishListId) {
        long deletedCount = factory.delete(wishList)
                .where(wishList.id.eq(wishListId))
                .execute();
        log.info("ID가 {}인 위시리스트 아이템을 삭제했습니다. 삭제된 개수: {}", wishListId, deletedCount);
    }

    public void deleteWishList(Long userId) {
        long deletedCount = factory.delete(wishList)
                .where(wishList.user.id.eq(userId))
                .execute();
        log.info("사용자 ID가 {}인 위시리스트를 삭제했습니다. 삭제된 개수: {}", userId, deletedCount);
    }
}