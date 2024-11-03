package com.back.orderservice.order.controller;

import com.back.orderservice.order.dto.OrderResponseDto;
import com.back.orderservice.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    static final Long userId = 1L;

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getOrders(HttpServletRequest request) {
        // Long userId = new ParseRequestUtil().extractUserIdFromRequest(request);

        return orderService.getOrders(userId);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOneOrder(
            @PathVariable("orderId")
            Long orderId,
            HttpServletRequest request) {
        // Long userId = new ParseRequestUtil().extractUserIdFromRequest(request);

        return orderService.getOneOrder(orderId, userId);
    }


/*
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
    } */

    /*

     */
/*

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
*/

    // @Transactional
    // @Scheduled(cron = "0 0 */3 * * *")
    // public void orderTimeCheck() {
    //     orderManagerService.orderTimeCheck();
    // }

}