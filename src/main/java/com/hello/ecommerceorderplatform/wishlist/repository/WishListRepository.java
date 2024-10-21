package com.hello.ecommerceorderplatform.wishlist.repository;

import com.hello.ecommerceorderplatform.user.domain.User;
import com.hello.ecommerceorderplatform.wishlist.domain.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    WishList findByUser(User user);
    
}
