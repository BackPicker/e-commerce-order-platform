package com.hello.ecommerceorderplatform.order.service;

import com.hello.ecommerceorderplatform.delivery.domain.Delivery;
import com.hello.ecommerceorderplatform.delivery.domain.DeliveryStatus;
import com.hello.ecommerceorderplatform.delivery.service.DeliveryService;
import com.hello.ecommerceorderplatform.item.domain.Item;
import com.hello.ecommerceorderplatform.item.exception.NosuchQuantityException;
import com.hello.ecommerceorderplatform.item.service.ItemService;
import com.hello.ecommerceorderplatform.order.domain.Order;
import com.hello.ecommerceorderplatform.order.domain.OrderItem;
import com.hello.ecommerceorderplatform.order.domain.OrderStatus;
import com.hello.ecommerceorderplatform.order.dto.CreateOrderResponseDto;
import com.hello.ecommerceorderplatform.order.dto.OrderItemResponseDto;
import com.hello.ecommerceorderplatform.order.dto.OrderRequestDto;
import com.hello.ecommerceorderplatform.order.dto.OrderResponseDto;
import com.hello.ecommerceorderplatform.order.repository.OrderItemRepository;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderManagerService {

    private final DeliveryService  deliveryService;
    private final WishListService  wishListService;
    private final ItemService      itemService;


    private final OrderRepository     orderRepository;
    private final OrderRepositoryImpl orderRepositoryImpl;

    private final OrderItemRepository orderItemRepository;


    @Transactional
    public CreateOrderResponseDto createOrder(OrderRequestDto orderRequestDto, User user) {
        log.info("주문 시작");

        // 위시리스트 가져오기 또는 생성
        WishList wishList = wishListService.getOrCreateWishList(user);
        if (wishList.getWishListItemList()
                .isEmpty()) {
            throw new WishListNotFoundException("위시 리스트가 비어 있습니다.");
        }

        log.info("총 주문금액 지정");
        List<OrderItem> orderItems = new ArrayList<>();

        log.info("orderItems 추가 로직 작동");
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

        log.info("배달 생성");
        Delivery delivery = new Delivery(user.getAddress(), DeliveryStatus.PAYMENT_PROCESSING);
        deliveryService.save(delivery);

        log.info("주문 생성");
        Order order = Order.createOrder(user, delivery, OrderStatus.ORDER_START, orderItems);
        orderRepository.save(order);

        log.info("주문 완료");
        return new CreateOrderResponseDto("결제가 완료되었습니다. 주문 ID: " + order.getId(), HttpStatus.CREATED.value());
    }

    public void save(Order order) {
        orderRepository.save(order);
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

        return new OrderResponseDto(user.getUsername(), order.getTotalOrderPrice(), orderItemRepository.findById(orderId)
                .stream()
                .map(item -> new OrderItemResponseDto(item.getTotalPrice(), item.getItem()
                        .getItemName(), item.getOrderCount()))
                .collect(Collectors.toList()), order.getOrderStatus());
    }



}