package com.hello.ecommerceorderplatform.order.controller;

import com.hello.ecommerceorderplatform.order.dto.CreateOrderResponseDto;
import com.hello.ecommerceorderplatform.order.dto.OrderRequestDto;
import com.hello.ecommerceorderplatform.order.service.OrderManagerService;
import com.hello.ecommerceorderplatform.order.service.OrderService;
import com.hello.ecommerceorderplatform.user.domain.User;
import com.hello.ecommerceorderplatform.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderManagerService orderManagerService;
    private final OrderService orderService;

    /**
     * 주문 목록 보기
     */
    public void getOrders() {
        User user = getCurrentUser();
        orderService.getOrders(user);
    }

    /**
     * 주문
     */
    @PostMapping
    public CreateOrderResponseDto createOrder(
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
