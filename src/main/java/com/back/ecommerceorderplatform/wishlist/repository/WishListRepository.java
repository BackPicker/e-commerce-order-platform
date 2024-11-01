package com.back.ecommerceorderplatform.wishlist.repository;

import com.back.ecommerceorderplatform.wishlist.domain.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishListRepository extends JpaRepository<WishList, Long> {

}
