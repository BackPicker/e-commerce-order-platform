package com.back.ecommerceorderplatform.wishlist.repository;

import com.back.ecommerceorderplatform.wishlist.domain.WishListItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishListItemRepository extends JpaRepository<WishListItem, Long> {
}
