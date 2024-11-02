package com.back.itemservice.repository;

import com.back.itemservice.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

    boolean existsByItemName(String itemName);

    Page<Item> findAllByOrderByCreatedAtDesc(Pageable pageable);

}
