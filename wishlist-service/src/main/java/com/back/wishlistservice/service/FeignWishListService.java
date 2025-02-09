package com.back.wishlistservice.service;

import com.back.wishlistservice.dto.Item;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "item-service")
public interface FeignWishListService {
    @GetMapping("/api/items/eureka/{itemId}")
    Item getItem(
            @PathVariable("itemId")
            Long itemId);
}