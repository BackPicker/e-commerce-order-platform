package com.hello.ecommerceorderplatform.order.controller;

import com.hello.ecommerceorderplatform.order.dto.OrderRequestDto;
import com.hello.ecommerceorderplatform.order.dto.OrderResponseDto;
import com.hello.ecommerceorderplatform.order.service.OrderManagerService;
import com.hello.ecommerceorderplatform.user.domain.User;
import com.hello.ecommerceorderplatform.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderManagerService orderManagerService;


    /**
     * 그냥 주문
     * 주문 + 위시리스트 주문 같이 주문
     */
    @PostMapping
    public OrderResponseDto createOrder(
            @RequestBody(required = false) OrderRequestDto orderRequestDto) {
        User user = getCurrentUser();
        log.info("user = {}", user);
        return orderManagerService.createOrder(orderRequestDto, user);
    }





    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }


}
