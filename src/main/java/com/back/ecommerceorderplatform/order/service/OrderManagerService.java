package com.back.ecommerceorderplatform.order.service;

import com.back.ecommerceorderplatform.delivery.domain.Delivery;
import com.back.ecommerceorderplatform.delivery.service.DeliveryService;
import com.back.ecommerceorderplatform.item.domain.Item;
import com.back.ecommerceorderplatform.item.exception.InsufficientStockException;
import com.back.ecommerceorderplatform.item.exception.NosuchQuantityException;
import com.back.ecommerceorderplatform.item.repository.ItemRepositoryImpl;
import com.back.ecommerceorderplatform.order.domain.Order;
import com.back.ecommerceorderplatform.order.domain.OrderItem;
import com.back.ecommerceorderplatform.order.domain.OrderStatus;
import com.back.ecommerceorderplatform.order.dto.CreateOrderResponseDto;
import com.back.ecommerceorderplatform.order.dto.OrderItemResponseDto;
import com.back.ecommerceorderplatform.order.dto.OrderRequestDto;
import com.back.ecommerceorderplatform.order.dto.OrderResponseDto;
import com.back.ecommerceorderplatform.order.exception.OrderTotalMismatchException;
import com.back.ecommerceorderplatform.order.repository.OrderItemRepository;
import com.back.ecommerceorderplatform.order.repository.OrderItemRepositoryImpl;
import com.back.ecommerceorderplatform.order.repository.OrderRepository;
import com.back.ecommerceorderplatform.order.repository.OrderRepositoryImpl;
import com.back.ecommerceorderplatform.user.domain.User;
import com.back.ecommerceorderplatform.wishlist.domain.WishList;
import com.back.ecommerceorderplatform.wishlist.domain.WishListItem;
import com.back.ecommerceorderplatform.wishlist.exception.WishListNotFoundException;
import com.back.ecommerceorderplatform.wishlist.service.WishListService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderManagerService {

    // Rate Limiter 설정
    private static final int REQUESTS_PER_SECOND = 300;
    private final DeliveryService deliveryService;
    private final WishListService wishListService;
    private final ItemRepositoryImpl itemRepositoryImpl;
    private final OrderRepository     orderRepository;
    private final OrderRepositoryImpl orderRepositoryImpl;
    private final OrderItemRepository     orderItemRepository;
    private final OrderItemRepositoryImpl orderItemRepositoryImpl;

    @Transactional
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
    }

    private OrderItem createOrderItem(WishListItem wishListItem) {
        Item    item       = wishListItem.getItem();
        Integer orderCount = wishListItem.getWishListItemQuantity();

        try {
            item.reduceQuantity(orderCount);
        } catch (NosuchQuantityException e) {
            throw new InsufficientStockException("아이템 " + item.getItemName() + "의 재고가 부족합니다.");
        }

        int totalPrice = wishListItem.totalWishListPrice(item, orderCount);
        return new OrderItem(item, totalPrice / orderCount, orderCount);
    }

    private void validateOrderTotal(int calculatedTotalPrice,
                                    int payment) {
        log.info("주문 금액 검증, calculatedTotalPrice = {}, payment = {}", calculatedTotalPrice, payment);

        if (payment != calculatedTotalPrice) {
            throw new OrderTotalMismatchException("주문 금액이 다릅니다. 실제 금액: " + calculatedTotalPrice + ", 입력 금액: " + payment);
        }
    }

    // 주문 리스트 보기
    public List<OrderResponseDto> getOrders(User user) {

        return orderRepositoryImpl.findByUserIdOrderByCreateDateDesc(user.getId());
    }

    // 하나의 주문 가져오기
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
    }


    @Transactional
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

    }

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


    }
}