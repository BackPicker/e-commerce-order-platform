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
        Long itemId = createOrderDTO.getItemId();
        Integer orderCount = createOrderDTO.getOrderCount();
        long payment = createOrderDTO.getPayment();

        String cacheKey = "itemId:" + itemId + ":quantity";
        String lockKey = "itemId:" + itemId + ":lock";

        // Redis에서 재고 수량 조회
        Integer redisItemQuantity = getItemStockFromRedis(cacheKey, lockKey, itemId);

        if (redisItemQuantity == null) {
            throw new RuntimeException("재고 정보를 가져오는 데 실패했습니다.");
        }

        Item item = feignOrderToItemService.eurekaItem(itemId);

        // 주문 수량이 재고보다 크거나 재고가 0인 경우 예외 처리
        if (redisItemQuantity < orderCount || redisItemQuantity == 0) {
            throw new InsufficientStockException("재고가 부족합니다. 현재 재고: " + redisItemQuantity);
        }

        long totalOrderPrice = orderCount * item.getPrice();

        // 결제 금액 확인
        if (payment != totalOrderPrice) {
            throw new IllegalArgumentException(
                    "결제 금액을 올바르게 입력하세요. 결제해야 할 금액은 " + totalOrderPrice + " 입니다, 주문 금액은 " + createOrderDTO.getPayment());
        }

        // Redis에서 재고 감소
        try {
            redisTemplate.opsForValue()
                    .decrement(cacheKey, orderCount);
        } catch (Exception e) {
            log.error("Redis에서 재고를 감소시키는 데 실패했습니다. error: {}", e.getMessage());
            throw new RuntimeException("재고를 감소시키는 데 실패했습니다. 시스템 관리자에게 문의하세요.");
        }

        // 주문 Entity 생성 및 저장
        Order order = new Order(userId, item.getItemId(), orderCount, totalOrderPrice);
        orderRepository.save(order);

        // Kafka 메시지 발송 (주문 생성 이벤트)
        kafkaTemplate.send("order_create", order);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(order)
                .statusCode(200)
                .resultMessage("주문이 성공했습니다.")
                .build();

        return ResponseEntity.ok(responseMessage);
    }

    /**
     * Redis에서 재고를 안전하게 가져오기 위한 메서드
     * Redisson을 사용하여 분산 락을 처리
     */
    private Integer getItemStockFromRedis(String cacheKey,
                                          String lockKey,
                                          Long itemId) {
        Integer redisItemQuantity = null;

        // Redisson을 사용하여 락을 획득
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 락을 10초 동안 기다리며 획득
            if (lock.tryLock(10, 10, TimeUnit.SECONDS)) {
                try {
                    // Redis에서 재고 가져오기
                    redisItemQuantity = (Integer) redisTemplate.opsForValue()
                            .get(cacheKey);

                    if (redisItemQuantity == null) {
                        // Redis에 값이 없으면 DB에서 가져오기
                        redisItemQuantity = feignOrderToItemService.getItemQuantity(itemId)
                                .getQuantity();
                        // Redis에 재고 값 저장
                        redisTemplate.opsForValue()
                                .set(cacheKey, redisItemQuantity);
                    }
                } catch (Exception e) {
                    log.error("Redis에서 재고 정보를 가져오는 중 오류 발생. error: {}", e.getMessage());
                    throw new RuntimeException("Redis에서 재고 정보를 가져오는 중 오류가 발생했습니다.");
                } finally {
                    // 락 해제
                    lock.unlock();
                }
            } else {
                log.warn("다른 프로세스가 Redis 잠금을 보유하고 있어 재고를 가져올 수 없습니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread()
                    .interrupt();  // 인터럽트 처리
            log.error("Redisson 락 대기 중 오류 발생: {}", e.getMessage());
        }

        return redisItemQuantity;
    }


    /**
     * 주문 취소 로직
     */
    @Transactional
    public ResponseEntity<ResponseMessage> cancelOrder(Long userId,
                                                       Long orderId) {
        // 주문을 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NullPointerException("존재하지 않는 주문 번호입니다."));

        // 주문의 소유자 확인
        if (!order.getUserId()
                .equals(userId)) {
            throw new SecurityException("이 주문에 접근할 권한이 없습니다.");
        }

        // Redis에서 아이템 ID와 관련된 재고를 갱신하기 위한 키 생성
        String cacheKey = "itemId:" + order.getItemId() + ":quantity";
        String lockKey = "itemId:" + order.getItemId() + ":lock";  // 분산 락을 위한 키

        // 주문 상태에 따라 다르게 처리
        switch (order.getOrderStatus()) {
            case PAYMENT_STATUS_COMPLETED:  // 결제 완료 상태
                // 주문 상태를 취소 상태로 변경
                order.updateOrderStatus(OrderStatus.ORDER_CANCELED_BY_USER);
                orderRepository.saveAndFlush(order);

                // Redis에서 재고 수량을 증가시키기
                adjustStockInRedis(cacheKey, lockKey, order.getItemId(), order.getOrderCount());

                return ResponseEntity.status(HttpStatus.OK)
                        .body(ResponseMessage.builder()
                                .statusCode(HttpStatus.OK.value())
                                .resultMessage("주문이 성공적으로 취소되었습니다.")
                                .build());

            case DELIVERY_IN_PROGRESS:  // 배송 중
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseMessage.builder()
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .resultMessage("현재 배송이 진행중이라 취소가 불가능합니다.")
                                .build());

            case DELIVERY_COMPLETED:  // 배송 완료
                if (Duration.between(order.getModifiedAt(), LocalDateTime.now())
                        .toDays() < 2) {
                    // 주문 상태를 취소 상태로 변경
                    order.updateOrderStatus(OrderStatus.ORDER_CANCELED_BY_USER);
                    orderRepository.saveAndFlush(order);

                    // Redis에서 재고 수량을 증가시키기
                    adjustStockInRedis(cacheKey, lockKey, order.getItemId(), order.getOrderCount());

                    return ResponseEntity.status(HttpStatus.OK)
                            .body(ResponseMessage.builder()
                                    .statusCode(HttpStatus.OK.value())
                                    .resultMessage("주문이 성공적으로 취소되었습니다.")
                                    .build());
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ResponseMessage.builder()
                                    .statusCode(HttpStatus.BAD_REQUEST.value())
                                    .resultMessage("기간이 지나 반품이 진행되지 않았습니다.")
                                    .build());
                }

            default:
                throw new RuntimeException("Order 상태 오류 발생");
        }
    }


    /**
     * Redis에서 재고 수량을 안전하게 증가시키는 메서드
     */
    private void adjustStockInRedis(String cacheKey,
                                    String lockKey,
                                    Long itemId,
                                    Integer orderCount) {
        // Redisson에서 락을 획득
        RLock lock = redissonClient.getLock(lockKey);  // RedissonClient를 통해 락을 가져옵니다.

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
                        order.updateOrderStatus(OrderStatus.DELIVERY_IN_PROGRESS);
                        break;
                    }
                    break;

                case DELIVERY_IN_PROGRESS:
                    if (Duration.between(order.getModifiedAt(), LocalDateTime.now())
                            .toDays() == 1) {
                        order.updateOrderStatus(OrderStatus.DELIVERY_COMPLETED);
                        break;
                    }
                    break;

                case ORDER_CANCELLATION_IN_PROGRESS:
                    if (Duration.between(order.getModifiedAt(), LocalDateTime.now())
                            .toDays() == 1) {
                        order.updateOrderStatus(OrderStatus.ORDER_CANCELED_BY_USER);
                        feignOrderToItemService.addItemQuantity(order.getItemId(), order.getOrderCount());
                        orderRepository.saveAndFlush(order);
                        break;
                    }
                    break;

                default:
                    throw new RuntimeException("orderTimeCheck 오류 발생");
            }
        }
    }


}