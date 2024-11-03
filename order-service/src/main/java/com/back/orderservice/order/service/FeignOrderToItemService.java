package com.back.orderservice.order.service;

import com.back.orderservice.order.dto.Item;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "item-service")
public interface FeignOrderToItemService {

    @GetMapping("/api/items/eureka/{itemId}")
    Item eurekaItem(
            @PathVariable("itemId")
            Long itemId);

}
