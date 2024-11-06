package com.back.orderservice.order.controller;

import com.back.common.dto.ResponseMessage;
import com.back.common.utils.ParseRequestUtil;
import com.back.orderservice.order.dto.CreateOrderDTO;
import com.back.orderservice.order.dto.OrderResponseDto;
import com.back.orderservice.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {


    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getOrders(HttpServletRequest request) {
        Long userId = new ParseRequestUtil().extractUserIdFromRequest(request);

        return orderService.getOrders(userId);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOneOrder(
            @PathVariable("orderId")
            Long orderId,
            HttpServletRequest request) {
        Long userId = new ParseRequestUtil().extractUserIdFromRequest(request);

        return orderService.getOneOrder(orderId, userId);
    }


    @PostMapping
    public ResponseEntity<ResponseMessage> createOrder(
            @RequestBody
            CreateOrderDTO createOrderDTO,
            HttpServletRequest request) {
        Long userId = new ParseRequestUtil().extractUserIdFromRequest(request);
        return orderService.createOrder(userId, createOrderDTO);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ResponseMessage> cancelOrder(
            @PathVariable
            Long orderId,
            HttpServletRequest request) {
        Long userId = new ParseRequestUtil().extractUserIdFromRequest(request);

        return orderService.cancelOrder(userId, orderId);
    }

    @Scheduled(cron = "0 0 */3 * * *")
    public void orderTimeCheck() {
        orderService.orderTimeCheck();
    }

}
