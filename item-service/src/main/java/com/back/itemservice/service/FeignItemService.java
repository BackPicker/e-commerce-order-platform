package com.back.itemservice.service;

import com.back.itemservice.dto.User;
import com.back.itemservice.dto.WishList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Queue;

@FeignClient(name = "gateway-service")
public interface FeignItemService {

    @GetMapping("/api/wishlist/eureka/getUser/{itemId}/")
    List<WishList> eurekaWishListByItemId(
            @PathVariable
            Long itemId);

    @GetMapping("/api/user/getQueue")
    Queue<User> eurekaGetUserByQueue(List<Long> userIdWishList);
}
