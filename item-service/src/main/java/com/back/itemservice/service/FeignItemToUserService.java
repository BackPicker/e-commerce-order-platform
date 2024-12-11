package com.back.itemservice.service;

import com.back.itemservice.dto.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Queue;

@FeignClient(name = "user-service")
public interface FeignItemToUserService {

    @GetMapping("/api/user/getQueue")
    Queue<User> eurekaGetUserByQueue(List<Long> userIdWishList);
}
