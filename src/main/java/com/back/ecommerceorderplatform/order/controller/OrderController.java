package com.back.ecommerceorderplatform.order.controller;

import com.back.common.dto.ResponseMessage;
import com.back.ecommerceorderplatform.order.dto.CreateOrderResponseDto;
import com.back.ecommerceorderplatform.order.dto.OrderRequestDto;
import com.back.ecommerceorderplatform.order.dto.OrderResponseDto;
import com.back.ecommerceorderplatform.order.service.OrderManagerService;
import com.back.ecommerceorderplatform.user.domain.User;
import com.back.ecommerceorderplatform.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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


    @GetMapping
    public ResponseEntity<ResponseMessage> getOrders() {
        User                   user   = getCurrentUser();
        List<OrderResponseDto> orders = orderManagerService.getOrders(user);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(orders)
                .statusCode(200)
                .resultMessage("주문 목록 불러오기 성공")
                .build();

        return ResponseEntity.ok(responseMessage);
    }


    @PostMapping
    public ResponseEntity<ResponseMessage> createOrder(
            @RequestBody
            OrderRequestDto orderRequestDto) {
        User                   user     = getCurrentUser();
        CreateOrderResponseDto response = orderManagerService.createOrder(orderRequestDto, user);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(response)
                .statusCode(201)  // 상태 코드 201 (Created)
                .resultMessage("주문 생성 성공")
                .build();

        return ResponseEntity.status(201)
                .body(responseMessage);
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<ResponseMessage> getOrder(
            @PathVariable("orderId")
            Long orderId) {
        User             user  = getCurrentUser();
        OrderResponseDto order = orderManagerService.getOrder(orderId, user);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(order)
                .statusCode(200)  // 상태 코드 200 (OK)
                .resultMessage("주문 조회 성공")
                .build();

        return ResponseEntity.ok(responseMessage);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable
            Long orderId) {
        User user = getCurrentUser();

        orderManagerService.cancelOrder(orderId, user);
        log.info("주문 ID {}가 성공적으로 취소되었습니다.", orderId);

        return ResponseEntity.noContent()
                .build(); // 204 No Content
    }


    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }


    @Transactional
    @Scheduled(cron = "0 0 */3 * * *")
    public void orderTimeCheck() {
        orderManagerService.orderTimeCheck();
    }

}
