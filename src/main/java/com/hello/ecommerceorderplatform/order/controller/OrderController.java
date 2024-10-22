package com.hello.ecommerceorderplatform.order.controller;

import com.hello.ecommerceorderplatform.order.dto.CreateOrderResponseDto;
import com.hello.ecommerceorderplatform.order.dto.OrderRequestDto;
import com.hello.ecommerceorderplatform.order.dto.OrderResponseDto;
import com.hello.ecommerceorderplatform.order.service.OrderManagerService;
import com.hello.ecommerceorderplatform.user.domain.User;
import com.hello.ecommerceorderplatform.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderManagerService orderManagerService;

    /**
     * 모든 주문 가져오기
     * @return
     */
    @GetMapping
    public List<OrderResponseDto> getOrders() {
        User user = getCurrentUser();
        List<OrderResponseDto> orders = orderManagerService.getOrders(user);

        return orders;
    }

    /**
     * 하나의 주문 가져오기
     *
     * @param orderId
     * @return
     */
    @GetMapping("/{orderId}")
    public OrderResponseDto getOrder(
            @PathVariable("orderId") Long orderId) {
        User user = getCurrentUser();
        return orderManagerService.getOrder(orderId, user);
    }


    /**
     * 주문 생성
     * @param orderRequestDto
     * @return
     */
    @PostMapping
    public CreateOrderResponseDto createOrder(
            @RequestBody(required = false) OrderRequestDto orderRequestDto) {
        User user = getCurrentUser();
        return orderManagerService.createOrder(orderRequestDto, user);
    }

    /**
     * 주문 취소
     */
    @DeleteMapping
    public void cancelOrder(Long orderId) {
        User user = getCurrentUser();
        orderManagerService.cancelOrder(orderId, user);

    }

    /**
     * User 가져오기
     * @return
     */
    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }

    /**
     * 주문 시간 확인
     */
    @Transactional
    @Scheduled(cron = "0 0 */3 * * *") //
    public void orderTimeCheck() {
        orderManagerService.orderTimeCheck();
    }


}
