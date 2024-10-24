package com.hello.ecommerceorderplatform.order.service;

import com.hello.ecommerceorderplatform.delivery.domain.Delivery;
import com.hello.ecommerceorderplatform.delivery.service.DeliveryService;
import com.hello.ecommerceorderplatform.item.domain.Item;
import com.hello.ecommerceorderplatform.item.exception.NosuchQuantityException;
import com.hello.ecommerceorderplatform.item.repository.ItemRepositoryImpl;
import com.hello.ecommerceorderplatform.order.domain.Order;
import com.hello.ecommerceorderplatform.order.domain.OrderItem;
import com.hello.ecommerceorderplatform.order.domain.OrderStatus;
import com.hello.ecommerceorderplatform.order.dto.CreateOrderResponseDto;
import com.hello.ecommerceorderplatform.order.dto.OrderItemResponseDto;
import com.hello.ecommerceorderplatform.order.dto.OrderRequestDto;
import com.hello.ecommerceorderplatform.order.dto.OrderResponseDto;
import com.hello.ecommerceorderplatform.order.repository.OrderItemRepository;
import com.hello.ecommerceorderplatform.order.repository.OrderItemRepositoryImpl;
import com.hello.ecommerceorderplatform.order.repository.OrderRepository;
import com.hello.ecommerceorderplatform.order.repository.OrderRepositoryImpl;
import com.hello.ecommerceorderplatform.user.domain.User;
import com.hello.ecommerceorderplatform.wishlist.domain.WishList;
import com.hello.ecommerceorderplatform.wishlist.domain.WishListItem;
import com.hello.ecommerceorderplatform.wishlist.exception.WishListNotFoundException;
import com.hello.ecommerceorderplatform.wishlist.service.WishListService;
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

    private final DeliveryService  deliveryService;
    private final WishListService  wishListService;

    private final ItemRepositoryImpl itemRepositoryImpl;


    private final OrderRepository     orderRepository;
    private final OrderRepositoryImpl orderRepositoryImpl;

    private final OrderItemRepository orderItemRepository;
    private final OrderItemRepositoryImpl orderItemRepositoryImpl;


    /**
     * 주문
     *
     * @param orderRequestDto
     * @param user
     * @return
     */
    @Transactional
    public CreateOrderResponseDto createOrder(OrderRequestDto orderRequestDto, User user) {
        log.info("주문 시작");

        // 위시리스트 가져오기 또는 생성
        WishList wishList = wishListService.getOrCreateWishList(user);
        if (wishList.getWishListItemList()
                .isEmpty()) {
            throw new WishListNotFoundException("위시 리스트가 비어 있습니다.");
        }

        // 총 주문금액 지정
        List<OrderItem> orderItems = new ArrayList<>();

        // orderItems 추가 로직 작동
        List<WishListItem> wishListItems = new ArrayList<>(wishList.getWishListItemList());

        for (WishListItem wishListItem : wishListItems) {
            Item    item               = wishListItem.getItem();
            Integer orderCount         = wishListItem.getWishListItemQuantity();
            int     wishListTotalPrice = wishListItem.totalWishListPrice(item, orderCount);

            // 재고 수량 감소 처리
            try {
                item.reduceQuantity(orderCount);
            } catch (NosuchQuantityException e) {
                throw new IllegalArgumentException("아이템 " + item.getItemName() + "의 재고가 부족합니다.");
            }

            OrderItem orderItem = new OrderItem(item, wishListTotalPrice / orderCount, orderCount); // 단가 계산
            orderItems.add(orderItem);
            orderItemRepository.save(orderItem);

            // 위시리스트 아이템 삭제
            wishList.removeWishListItem(wishListItem); // 위시리스트에서 아이템 제거
        }

        // 위시리스트 삭제
        wishListService.removeWishList(user.getId()); // 이 메서드는 위시리스트를 삭제하는 메서드입니다.

        // 주문 금액 검증 로직
        int calculatedTotalPrice = orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();

        log.info("주문 금액 검증 로직 작동, calculatedTotalPrice = {}, payment = {}", calculatedTotalPrice, orderRequestDto.getPayment());

        if (orderRequestDto.getPayment() != calculatedTotalPrice) {
            throw new IllegalArgumentException("주문 금액이 다릅니다. 실제 금액: " + calculatedTotalPrice + ", 입력 금액: " + orderRequestDto.getPayment());
        }

        // 배달 생성
        Delivery delivery = new Delivery(user.getAddress());
        deliveryService.save(delivery);

        // 주문 생성
        Order order = Order.createOrder(user, delivery, OrderStatus.PAYMENT_COMPLETED, orderItems);
        orderRepository.save(order);

        // 주문 완료
        return new CreateOrderResponseDto("결제가 완료되었습니다. 주문 ID: " + order.getId(), HttpStatus.CREATED.value());
    }


    // 주문 리스트 보기
    public List<OrderResponseDto> getOrders(User user) {

        return orderRepositoryImpl.findByUserIdOrderByCreateDateDesc(user.getId());
    }

    // 하나의 주문 가져오기
    public OrderResponseDto getOrder(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NullPointerException("존재하지 않는 주문 번호입니다."));

        if (!order.getUser()
                .getId()
                .equals(user.getId())) {
            throw new SecurityException("이 주문에 접근할 권한이 없습니다.");
        }
        List<OrderItemResponseDto> orderItems = orderItemRepositoryImpl.getOrder(orderId);

        return new OrderResponseDto(user.getUsername(), order.getTotalOrderPrice(), orderItems, order.getOrderStatus());
    }


    public ResponseEntity<CreateOrderResponseDto> cancelOrder(Long orderId, User user) {
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
                    order.updateOrderStatus(OrderStatus.CANCELED_BY_USER);

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