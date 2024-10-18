package com.hello.ecommerceorderplatform.item.repository;

import com.hello.ecommerceorderplatform.item.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
