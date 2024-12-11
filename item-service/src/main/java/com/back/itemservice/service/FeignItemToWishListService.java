package com.back.itemservice.service;

import com.back.itemservice.dto.WishList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "wishlist-service")
public interface FeignItemToWishListService {

    @GetMapping("/api/wishlist/eureka/getUser/{itemId}/")
    List<WishList> eurekaWishListByItemId(
            @PathVariable
            Long itemId);

}
