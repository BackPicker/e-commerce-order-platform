package com.back.ecommerceorderplatform.item.repository;

import com.back.ecommerceorderplatform.item.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
