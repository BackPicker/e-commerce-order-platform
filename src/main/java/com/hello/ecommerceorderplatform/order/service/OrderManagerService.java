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
import com.hello.ecommerceorderplatform.order.dto.OrderRequestDto;
import com.hello.ecommerceorderplatform.order.dto.OrderResponseDto;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderManagerService {

    private final OrderService    orderService;
    private final DeliveryService deliveryService;
    private final WishListService wishListService;
    private final ItemService     itemService;

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto, User user) {
        log.info("주문 시작");
        WishList wishList = wishListService.getOrCreateWishList(user);

        if (wishList.getWishListItemList()
                .isEmpty()) {
            throw new WishListNotFoundException("위시 리스트를 찾을 수 없습니다");
        }

        log.info("총 주문금액 지정");
        int             totalPrice = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        log.info("orderItems 추가 로직 작동");
        for (WishListItem wishListItem : wishList.getWishListItemList()) {
            Item    item               = wishListItem.getItem();
            Integer orderCount         = wishListItem.getWishListItemQuantity();
            int     wishListTotalPrice = wishListItem.totalWishListPrice(item, orderCount);
            totalPrice += wishListTotalPrice;

            try {
                item.reduceQuantity(orderCount);
            } catch (NosuchQuantityException e) {
                throw new IllegalArgumentException("재고가 부족합니다.");
            }

            OrderItem orderItem = new OrderItem(item, wishListTotalPrice, orderCount);
            orderItems.add(orderItem);
            wishListService.deleteWishListItem(wishListItem);
        }

        log.info("주문 금액 검증 로직 작동, totalPrice = {}, payment = {}", totalPrice, orderRequestDto.getPayment());
        if (orderRequestDto.getPayment() != totalPrice) {
            // throw new IllegalArgumentException("주문 금액이 다릅니다");
        }

        log.info("배달 생성");
        // 배달 객체 생성
        Delivery delivery = new Delivery(user.getAddress(), DeliveryStatus.PAYMENT_PROCESSING);
        // Delivery를 먼저 저장
        deliveryService.save(delivery); // Delivery 객체를 데이터베이스에 저장

        log.info("주문 생성");
        // 주문 생성
        Order order = Order.createOrder(user, delivery, OrderStatus.ORDER_START, orderItems);

        // 이제 Order를 저장
        orderService.save(order);

        log.info("주문 완료");
        return new OrderResponseDto("결제가 완료되었습니다", HttpStatus.CREATED.value());
    }
}