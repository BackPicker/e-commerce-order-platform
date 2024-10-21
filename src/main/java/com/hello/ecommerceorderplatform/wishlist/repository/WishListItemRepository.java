package com.hello.ecommerceorderplatform.wishlist.repository;

import com.hello.ecommerceorderplatform.wishlist.domain.WishListItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishListItemRepository extends JpaRepository<WishListItem, Long> {
}
