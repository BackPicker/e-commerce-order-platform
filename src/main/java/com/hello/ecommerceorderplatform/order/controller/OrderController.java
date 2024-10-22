package com.hello.ecommerceorderplatform.order.controller;

import com.hello.ecommerceorderplatform.order.dto.CreateOrderResponseDto;
import com.hello.ecommerceorderplatform.order.dto.OrderRequestDto;
import com.hello.ecommerceorderplatform.order.dto.OrderResponseDto;
import com.hello.ecommerceorderplatform.order.service.OrderManagerService;
import com.hello.ecommerceorderplatform.user.domain.User;
import com.hello.ecommerceorderplatform.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderManagerService orderManagerService;

    /**
     *  모든 주문 목록 보기
     */
    @GetMapping
    public List<OrderResponseDto> getOrders() {
        User user = getCurrentUser();
        List<OrderResponseDto> orders = orderManagerService.getOrders(user);

        return orders;
    }

    @GetMapping("/{orderId}")
    public void getOrder(
            @PathVariable("orderId") Long orderId) {
        User user = getCurrentUser();
        orderManagerService.getOrder(orderId, user);

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
