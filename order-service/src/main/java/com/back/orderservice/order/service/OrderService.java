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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository               orderRepository;
    private final FeignOrderToItemService       feignOrderToItemService;

    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, Order>  kafkaTemplate;

    private static final int MAX_RETRIES = 3;      // 잠금 획득을 위한 최대 재시도 횟수
    private static final int WAIT_TIME   = 1;        // 잠금 대기 시간 (초)
    private static final int LEASE_TIME  = 5;       // 잠금 유지 시간 (초)

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


    @Transactional
    public ResponseEntity<ResponseMessage> createOrder(Long userId,
                                                       CreateOrderDTO createOrderDTO) {
        Long itemId = createOrderDTO.getItemId();
        Integer orderCount = createOrderDTO.getOrderCount();
        long payment = createOrderDTO.getPayment();

        String cacheKey = "itemId:" + itemId + ":quantity";
        String lockKey = "itemId:" + itemId + ":lock";

        // 재고 조회 및 검증
        Integer redisItemQuantity = fetchStockWithLock(cacheKey, lockKey, itemId);

        if (redisItemQuantity == null || redisItemQuantity < orderCount) {
            throw new InsufficientStockException("재고가 부족합니다. 현재 재고: " + (redisItemQuantity == null ? 0 : redisItemQuantity));
        }

        // 상품 정보 조회 및 결제 금액 검증
        Item item = feignOrderToItemService.eurekaItem(itemId);

        long totalOrderPrice = orderCount * item.getPrice();
        if (payment != totalOrderPrice) {
            throw new IllegalArgumentException("결제 금액을 올바르게 입력하세요. 결제해야 할 금액은 " + totalOrderPrice + "입니다.");
        }

        // 재고 감소
        decreaseStockWithLock(cacheKey, lockKey, orderCount);

        // 주문 생성
        Order order = new Order(userId, itemId, orderCount, totalOrderPrice);

        // Kafka 메시지 발송
        try {
            kafkaTemplate.send("order_create", order);
        } catch (Exception e) {
            log.error("Kafka 메시지 발송 실패: {}", e.getMessage());
        }

        // 응답 반환
        return ResponseEntity.ok(ResponseMessage.builder()
                .data(order)
                .statusCode(200)
                .resultMessage("주문이 성공했습니다.")
                .build());
    }

    private Integer fetchStockWithLock(String cacheKey,
                                       String lockKey,
                                       Long itemId) {
        // 잠금 획득을 위한 재시도 로직 및 잠금 시간 단축
        for (int i = 0; i < MAX_RETRIES; i++) {
            RLock lock = redissonClient.getLock(lockKey);
            try {
                if (lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS)) {

                    Integer stock = (Integer) redisTemplate.opsForValue()
                            .get(cacheKey);
                    if (stock == null) {
                        stock = feignOrderToItemService.getItemQuantity(itemId)
                                .getQuantity();
                        redisTemplate.opsForValue()
                                .set(cacheKey, stock);
                    }
                    return stock;

                }
            } catch (InterruptedException e) {
                Thread.currentThread()
                        .interrupt();
                throw new RuntimeException("재고 잠금 중 오류 발생", e);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
        throw new RuntimeException("잠금 획득 실패: 재고 잠금을 가져올 수 없습니다.");
    }

    // 초기 재고 설정을 위한 메서드 분리

    private void decreaseStockWithLock(String cacheKey,
                                       String lockKey,
                                       Integer orderCount) {
        // 잠금 획득을 위한 재시도 로직
        for (int i = 0; i < MAX_RETRIES; i++) {
            RLock lock = redissonClient.getLock(lockKey);
            try {
                if (lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS)) {
                    redisTemplate.opsForValue()
                            .decrement(cacheKey, orderCount);
                    return;
                }
            } catch (InterruptedException e) {
                Thread.currentThread()
                        .interrupt();
                throw new RuntimeException("재고 감소 잠금 중 오류 발생", e);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
        throw new RuntimeException("잠금 획득 실패: 재고 감소 잠금을 가져올 수 없습니다.");
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

        String cacheKey = "itemId:" + order.getItemId() + ":quantity";
        String lockKey = "itemId:" + order.getItemId() + ":lock";

        switch (order.getOrderStatus()) {
            case PAYMENT_STATUS_COMPLETED:
                cancelOrderAndUpdateStock(order, cacheKey, lockKey);
                return buildResponse(HttpStatus.OK, "주문이 성공적으로 취소되었습니다.");

            case DELIVERY_IN_PROGRESS:
                return buildResponse(HttpStatus.BAD_REQUEST, "현재 배송이 진행중이라 취소가 불가능합니다.");

            case DELIVERY_COMPLETED:
                if (Duration.between(order.getModifiedAt(), LocalDateTime.now())
                        .toDays() < 2) {
                    cancelOrderAndUpdateStock(order, cacheKey, lockKey);
                    return buildResponse(HttpStatus.OK, "주문이 성공적으로 취소되었습니다.");
                }
                return buildResponse(HttpStatus.BAD_REQUEST, "기간이 지나 반품이 진행되지 않았습니다.");

            default:
                throw new RuntimeException("Order 상태 오류 발생");
        }
    }

    private void cancelOrderAndUpdateStock(Order order,
                                           String cacheKey,
                                           String lockKey) {
        order.updateOrderStatus(OrderStatus.ORDER_CANCELED_BY_USER);
        orderRepository.saveAndFlush(order);
        adjustStockInRedis(cacheKey, lockKey, order.getItemId(), order.getOrderCount());
    }

    private ResponseEntity<ResponseMessage> buildResponse(HttpStatus status,
                                                          String message) {
        return ResponseEntity.status(status)
                .body(ResponseMessage.builder()
                        .statusCode(status.value())
                        .resultMessage(message)
                        .build());
    }


    /**
     * Redis에서 재고 수량 증가
     */
    private void adjustStockInRedis(String cacheKey,
                                    String lockKey,
                                    Long itemId,
                                    Integer orderCount) {
        // Redisson에서 락을 획득
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 락을 10초 동안 기다리며 획득
            if (lock.tryLock(10, 10, TimeUnit.SECONDS)) {
                try {
                    // Redis에서 아이템의 수량을 가져옴
                    Integer currentStock = (Integer) redisTemplate.opsForValue()
                            .get(cacheKey);

                    if (currentStock != null) {
                        // 현재 재고가 존재하면 재고를 증가시킴
                        redisTemplate.opsForValue()
                                .increment(cacheKey, orderCount);
                    } else {
                        // Redis에 재고 값이 없다면 DB에서 재고를 가져오고, Redis에 저장
                        Integer dbStock = feignOrderToItemService.getItemQuantity(itemId)
                                .getQuantity();
                        redisTemplate.opsForValue()
                                .set(cacheKey, dbStock + orderCount);
                    }
                } catch (Exception e) {
                    log.error("Redis에서 재고 수량을 업데이트하는 중 오류 발생: {}", e.getMessage());
                    throw new RuntimeException("재고를 업데이트하는 데 실패했습니다.");
                }
            } else {
                log.warn("다른 프로세스가 Redis 잠금을 보유하고 있어 재고를 업데이트할 수 없습니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread()
                    .interrupt();  // 인터럽트 처리
            log.error("Redisson 락 대기 중 인터럽트 발생: {}", e.getMessage());
        } finally {
            // 락을 해제
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
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
                        String cacheKey = "itemId:" + order.getItemId() + ":quantity";
                        order.updateOrderStatus(OrderStatus.DELIVERY_IN_PROGRESS);


                        break;
                    }
                    break;

                case DELIVERY_IN_PROGRESS:
                    if (Duration.between(order.getModifiedAt(), LocalDateTime.now())
                            .toDays() == 1) {
                        order.updateOrderStatus(OrderStatus.DELIVERY_COMPLETED);
                        String cacheKey = "itemId:" + order.getItemId() + ":quantity";
                        break;
                    }
                    break;

                case ORDER_CANCELLATION_IN_PROGRESS:
                    if (Duration.between(order.getModifiedAt(), LocalDateTime.now())
                            .toDays() == 1) {
                        order.updateOrderStatus(OrderStatus.ORDER_CANCELED_BY_USER);
                        break;
                    }
                    break;

                default:
                    throw new RuntimeException("orderTimeCheck 오류 발생");
            }
        }
    }


}