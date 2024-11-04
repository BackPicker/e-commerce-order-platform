package com.back.orderservice.order.service;

import com.back.orderservice.order.dto.Item;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "item-service")
public interface FeignOrderToItemService {

    @GetMapping("/api/items/eureka/{itemId}")
    Item eurekaItem(
            @PathVariable("itemId")
            Long itemId);

    @PutMapping("/api/items/eureka/{itemId}/reduce/{quantity}")
    void reduceItemQuantity(
            @PathVariable("itemId")
            Long itemId,
            @PathVariable("quantity")
            Integer quantity);

    @PutMapping("/api/items/eureka/{itemId}/add/{orderCount}")
    void addItemQuantity(
            @PathVariable("itemId")
            Long itemId,
            @PathVariable("orderCount")
            Integer orderCount);
}
