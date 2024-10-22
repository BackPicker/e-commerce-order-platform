package com.hello.ecommerceorderplatform.order.domain;

import com.hello.ecommerceorderplatform.delivery.domain.Delivery;
import com.hello.ecommerceorderplatform.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 ManyToOne 관계
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // 주문한 상품들, 하나의 주문에 여러 ITEM 들이 담길 수 있다.
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<OrderItem> orderItems = new ArrayList<>();

    @CreatedDate
    private LocalDateTime orderDate;  // 주문 날짜

    @JoinColumn(name = "delivery_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Delivery delivery;   // OneToOne 관계

    /**
     * 1
     * 주문 상태
     * 주문, 사용자에 의한 주문취소, 품절에 의한 주문취소
     */
    @Setter
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public Order(OrderStatus orderStatus, Delivery delivery, LocalDateTime orderDate, User user) {
        this.orderStatus = orderStatus;
        this.delivery = delivery;
        this.orderDate   = orderDate;
        this.user        = user;
    }


    public static Order createOrder(User user, Delivery delivery, OrderStatus orderStatus, List<OrderItem> orderItems) {
        Order order = new Order(orderStatus, delivery, LocalDateTime.now(), user);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        return order;
    }

    private void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
}
