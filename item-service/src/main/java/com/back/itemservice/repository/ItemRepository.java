package com.back.itemservice.repository;

import com.back.itemservice.domain.Item;
import feign.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface ItemRepository extends JpaRepository<Item, Long> {

    boolean existsByItemName(String itemName);

    Page<Item> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Item i where i.id = :itemId")
    Item customFindById(
            @Param("itemId")
            Long itemId);

    @Query("select i.quantity from Item  i where i.id = :itemId")
    Integer findItemQuantityByItemId(@Param("itemId") Long itemId);
}
