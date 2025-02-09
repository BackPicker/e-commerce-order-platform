package com.back.wishlistservice.repository;

import com.back.wishlistservice.domain.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    List<WishList> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUserIdAndItemId(Long userId,
                                    Long itemId);

    WishList findByUserIdAndItemId(Long userId,
                                   Long id);

    List<WishList> findAllByItemId(Long itemId);

}
