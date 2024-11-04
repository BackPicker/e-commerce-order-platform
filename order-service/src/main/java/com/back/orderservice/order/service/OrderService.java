package com.back.orderservice.order.service;

import com.back.common.dto.ResponseMessage;
import com.back.common.dto.order.CreateOrderReqDto;
import com.back.orderservice.order.domain.Order;
import com.back.orderservice.order.dto.CreateOrderDTO;
import com.back.orderservice.order.dto.Item;
import com.back.orderservice.order.dto.OrderResponseDto;
import com.back.orderservice.order.repository.OrderItemRepository;
import com.back.orderservice.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository     orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final FeignOrderToItemService feignOrderToItemService;


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





/*     @Transactional
    public CreateOrderResponseDto createOrder(OrderRequestDto orderRequestDto,
                                              User user) {
        log.info("주문 시작");

        // 위시리스트 가져오기 또는 생성
        WishList wishList = wishListService.getOrCreateWishList(user);
        if (wishList.getWishListItemList()
                .isEmpty()) {
            throw new WishListNotFoundException("위시 리스트가 비어 있습니다.");
        }

        // 총 주문금액 지정
        List<OrderItem>    orderItems    = new ArrayList<>();
        List<WishListItem> wishListItems = new ArrayList<>(wishList.getWishListItemList());

        // 총 계산할 금액 변수
        int calculatedTotalPrice = 0;

        for (WishListItem wishListItem : wishListItems) {
            OrderItem orderItem = createOrderItem(wishListItem);
            orderItems.add(orderItem);
            orderItemRepository.save(orderItem);

            // 총 가격 누적
            calculatedTotalPrice += orderItem.getTotalPrice();
        }

        // 주문 금액 검증
        validateOrderTotal(calculatedTotalPrice, orderRequestDto.getPayment());

        // 위시리스트 삭제
        wishListService.removeWishList(user.getId());

        // 배달 생성
        Delivery delivery = new Delivery(user.getAddress());
        deliveryService.save(delivery);

        // 주문 생성
        Order order = Order.createOrder(user, delivery, OrderStatus.PAYMENT_COMPLETED, orderItems);
        orderRepository.save(order);

        log.info("주문 완료, 주문 ID: {}", order.getId());
        return new CreateOrderResponseDto("결제가 완료되었습니다. 주문 ID: " + order.getId(), HttpStatus.CREATED.value());
    } */

/*     private OrderItem createOrderItem(WishListItem wishListItem) {
        Item    item       = wishListItem.getItem();
        Integer orderCount = wishListItem.getWishListItemQuantity();

        try {
            item.reduceQuantity(orderCount);
        } catch (NosuchQuantityException e) {
            throw new InsufficientStockException("아이템 " + item.getItemName() + "의 재고가 부족합니다.");
        }

        int totalPrice = wishListItem.totalWishListPrice(item, orderCount);
        return new OrderItem(item, totalPrice / orderCount, orderCount);
    } */

/*
    private void validateOrderTotal(int calculatedTotalPrice,
                                    int payment) {
        log.info("주문 금액 검증, calculatedTotalPrice = {}, payment = {}", calculatedTotalPrice, payment);

        if (payment != calculatedTotalPrice) {
            throw new OrderTotalMismatchException("주문 금액이 다릅니다. 실제 금액: " + calculatedTotalPrice + ", 입력 금액: " + payment);
        }
    } */



/*     // 하나의 주문 가져오기
    public OrderResponseDto getOrder(Long orderId,
                                     User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NullPointerException("존재하지 않는 주문 번호입니다."));

        if (!order.getUser()
                .getId()
                .equals(user.getId())) {
            throw new SecurityException("이 주문에 접근할 권한이 없습니다.");
        }
        List<OrderItemResponseDto> orderItems = orderItemRepositoryImpl.getOrderItemsByOrderId(orderId);

        return new OrderResponseDto(user.getUsername(), order.getTotalOrderPrice(), orderItems, order.getOrderStatus());
    } */


/*     @Transactional
    public ResponseEntity<CreateOrderResponseDto> cancelOrder(Long orderId,
                                                              User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NullPointerException("존재하지 않는 주문 번호입니다."));
        if (!order.getUser()
                .getId()
                .equals(user.getId())) {
            throw new SecurityException("이 주문에 접근할 권한이 없습니다.");
        }

        switch (order.getOrderStatus()) {

            case IN_DELIVERY:
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new CreateOrderResponseDto("배송중인 상품입니다", HttpStatus.BAD_REQUEST.value()));

            case DELIVERY_COMPLETED:
                if (Duration.between(order.getModifiedAt(), LocalDateTime.now())
                        .toDays() < 2) {
                    order.updateOrderStatus(OrderStatus.ORDER_CANCELED_BY_USER);

                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new CreateOrderResponseDto("반품을 진행합니다", HttpStatus.OK.value()));

                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new CreateOrderResponseDto("반품 기한이 지났습니다", HttpStatus.BAD_REQUEST.value()));
                }
            case PAYMENT_COMPLETED:
                List<OrderItem> orderItems = order.getOrderItems();
                for (OrderItem orderItem : orderItems) {
                    // 아이템
                    Item item = orderItem.getItem();
                    // 주문 수량
                    int orderCount = orderItem.getOrderCount();
                    itemRepositoryImpl.addItemQuantity(item.getId(), orderCount);
                }
                orderRepository.delete(order);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new CreateOrderResponseDto("주문이 취소되었습니다", HttpStatus.OK.value()));

            default:
                throw new RuntimeException("OrderStatus 오류 발생");
        }
    } */
/*
    public void orderTimeCheck() {
        List<Order> orderList = orderRepository.findAll();
        for (Order order : orderList) {
            switch (order.getOrderStatus()) {
                case PAYMENT_COMPLETED:
                    if (Duration.between(order.getCreatedAt(), LocalDateTime.now())
                            .toDays() == 1) order.updateOrderStatus(OrderStatus.IN_DELIVERY);

                case IN_DELIVERY:
                    if (Duration.between(order.getModifiedAt(), LocalDateTime.now())
                            .toDays() == 1) order.updateOrderStatus(OrderStatus.DELIVERY_COMPLETED);

                case DELIVERY_CANCELED_BY_USER:
                    if (Duration.between(order.getModifiedAt(), LocalDateTime.now())
                            .toDays() == 1) {
                        order.updateOrderStatus(OrderStatus.DELIVERY_CANCELED);

                        List<OrderItem> orderItemList = order.getOrderItems();

                        for (OrderItem orderItem : orderItemList)
                            itemRepositoryImpl.addItemQuantity(orderItem.getItem()
                                    .getId(), orderItem.getOrderCount());
                    }
                default:
                    throw new RuntimeException("orderTimeCheck 오류 발생");
            }
        }
    } */


}