package com.back.orderservice.order.service;


import com.back.common.dto.ResponseMessage;
import com.back.orderservice.order.domain.Order;
import com.back.orderservice.order.domain.OrderStatus;
import com.back.orderservice.order.dto.CreateOrderDTO;
import com.back.orderservice.order.dto.Item;
import com.back.orderservice.order.dto.OrderResponseDto;
import com.back.orderservice.order.exception.InsufficientStockException;
import com.back.orderservice.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository               orderRepository;
    private final FeignOrderToItemService       feignOrderToItemService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, Order>  kafkaTemplate;


    /**
     * 주문 리스트 보기
     */
    public ResponseEntity<List<OrderResponseDto>> getOrders(Long userId) {
        List<Order> orderList = orderRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        if (orderList.isEmpty()) {
            throw new NoSuchElementException("주문이 없습니다.");
        }

        List<OrderResponseDto> orderResponseDtoList = new ArrayList<>();
        for (Order order : orderList) {
            Item             item             = feignOrderToItemService.eurekaItem(order.getItemId());
            OrderResponseDto orderResponseDto = OrderResponseDto.entityToDTO(order, item, userId);
            orderResponseDtoList.add(orderResponseDto);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(orderResponseDtoList);
    }


    /**
     * 주문 단건 조회
     */
    public ResponseEntity<OrderResponseDto> getOneOrder(Long orderId,
                                                        Long userId) {
        log.info("orderId = {}, userId = {}", orderId, userId);

        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                // .get();
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 주문 번호입니다."));

        // 주문이 해당 사용자에 속하는지 확인
        if (!order.getUserId()
                .equals(userId)) {
            throw new IllegalArgumentException("이 주문은 해당 사용자에게 속하지 않습니다.");
        }

        Item             item             = feignOrderToItemService.eurekaItem(order.getItemId());
        OrderResponseDto orderResponseDto = OrderResponseDto.entityToDTO(order, item, userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderResponseDto);
    }


    /**
     * 주문하기
     */
    @Transactional
    public ResponseEntity<ResponseMessage> createOrder(Long userId,
                                                       CreateOrderDTO createOrderDTO) {
        Long    itemId     = createOrderDTO.getItemId();
        Integer orderCount = createOrderDTO.getOrderCount();
        long    payment    = createOrderDTO.getPayment();

        // RedisTemplate을 사용하여 데이터 가져오기
        String cacheKey = "itemId:" + itemId + ":quantity";
        Integer redisItemQuantity = (Integer) redisTemplate.opsForValue()
                .get(cacheKey);

        if (redisItemQuantity == null) {
            // Redis에 값이 없으면, feign을 통해 재고 수량 가져오기
            redisItemQuantity = feignOrderToItemService.getItemQuantity(itemId)
                    .getQuantity();

            // Redis에 값을 저장할 때 Integer 값을 저장
            redisTemplate.opsForValue()
                    .set(cacheKey, redisItemQuantity);
        }

        Item item = feignOrderToItemService.eurekaItem(itemId);

        // 주문 수량이 재고보다 크거나, redis 수 량이 0인 경우
        if (redisItemQuantity < orderCount || redisItemQuantity == 0) {
            throw new InsufficientStockException("재고가 부족합니다. 현재 재고: " + redisItemQuantity);
        }

        long totalOrderPrice = orderCount * item.getPrice();

        // 결제 시도
        if (payment != totalOrderPrice) {
            throw new IllegalArgumentException(
                    "결제 금액을 올바르게 입력하세요 결제해야 할 금액은 " + totalOrderPrice + " 입니다, 주문 금액은 " + createOrderDTO.getPayment());
        }

        // Redis의 재고 감소
        redisTemplate.opsForValue()
                .decrement(cacheKey, orderCount);

        // 재고 감소 후 주문 저장
        // feignOrderToItemService.reduceItemQuantity(itemId, orderCount);
        // User 의 Money 추가해서, 감소시키는 로직 추가?

        // 주문 Entity 만들고 저장
        Order order = new Order(userId, item.getItemId(), orderCount, totalOrderPrice);
        kafkaTemplate.send("order_create", order);

        ResponseMessage build = ResponseMessage.builder()
                .data(order)
                .statusCode(200)
                .resultMessage("주문이 성공했습니다")
                .build();
        return ResponseEntity.ok(build);
    }


    @Transactional
    public ResponseEntity<ResponseMessage> cancelOrder(Long userId,
                                                       Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NullPointerException("존재하지 않는 주문 번호입니다."));

        if (!order.getUserId()
                .equals(userId)) {
            throw new SecurityException("이 주문에 접근할 권한이 없습니다.");
        }

        switch (order.getOrderStatus()) {

            // 0일차, 결제 완료
            case PAYMENT_STATUS_COMPLETED:
                order.updateOrderStatus(OrderStatus.ORDER_CANCELED_BY_USER);
                orderRepository.saveAndFlush(order);

                return ResponseEntity.status(HttpStatus.OK)
                        .body(ResponseMessage.builder()
                                .statusCode(HttpStatus.OK.value())
                                .resultMessage("반품이 성공적으로 진행되었습니다")
                                .build());

            // 1일차, 배송 중
            case DELIVERY_IN_PROGRESS:
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ResponseMessage.builder()
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .resultMessage("현재 배송이 진행중이라 취소가 불가능 합니다")
                                .build());

            // 2일차, 배송 완료
            case DELIVERY_COMPLETED:
                if (Duration.between(order.getModifiedAt(), LocalDateTime.now())
                        .toDays() < 2) {
                    order.updateOrderStatus(OrderStatus.ORDER_CANCELED_BY_USER);
                    orderRepository.saveAndFlush(order);

                    return ResponseEntity.status(HttpStatus.OK)
                            .body(ResponseMessage.builder()
                                    .statusCode(HttpStatus.OK.value())
                                    .resultMessage("반품이 성공적으로 진행되었습니다")
                                    .build());

                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ResponseMessage.builder()
                                    .statusCode(HttpStatus.BAD_REQUEST.value())
                                    .resultMessage("기간이 지나 반품이 진행되지 않았습니다")
                                    .build());
                }
            default:
                throw new RuntimeException("Order 오류 발생");
        }
    }

    @Transactional
    public void orderTimeCheck() {
        List<Order> orderList = orderRepository.findAll();
        for (Order order : orderList) {

            switch (order.getOrderStatus()) {

                case PAYMENT_STATUS_COMPLETED:
                    if (Duration.between(order.getCreatedAt(), LocalDateTime.now())
                            .toDays() == 1) {
                        order.updateOrderStatus(OrderStatus.DELIVERY_IN_PROGRESS);
                    }

                case DELIVERY_IN_PROGRESS:
                    if (Duration.between(order.getModifiedAt(), LocalDateTime.now())
                            .toDays() == 1) {
                        order.updateOrderStatus(OrderStatus.DELIVERY_COMPLETED);
                    }

                case ORDER_CANCELLATION_IN_PROGRESS:
                    if (Duration.between(order.getModifiedAt(), LocalDateTime.now())
                            .toDays() == 1) {
                        order.updateOrderStatus(OrderStatus.ORDER_CANCELED_BY_USER);
                        feignOrderToItemService.addItemQuantity(order.getItemId(), order.getOrderCount());
                        orderRepository.saveAndFlush(order);
                    }
                default:
                    throw new RuntimeException("orderTimeCheck 오류 발생");
            }
        }
    }


}