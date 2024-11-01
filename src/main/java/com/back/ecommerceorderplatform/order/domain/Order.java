package com.back.ecommerceorderplatform.order.domain;

import com.back.common.entity.BaseEntity;
import com.back.ecommerceorderplatform.delivery.domain.Delivery;
import com.back.ecommerceorderplatform.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<OrderItem> orderItems = new ArrayList<>();


    @JoinColumn(name = "delivery_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Delivery delivery;   // OneToOne 관계

    @Setter
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Setter
    @Column(nullable = false)
    private int totalOrderPrice; // 주문 총 금액 추가

    public Order(OrderStatus orderStatus,
                 Delivery delivery,
                 User user) {
        this.orderStatus = orderStatus;
        this.delivery    = delivery;
        this.user        = user;
    }

    public static Order createOrder(User user,
                                    Delivery delivery,
                                    OrderStatus orderStatus,
                                    List<OrderItem> orderItems) {
        Order order      = new Order(orderStatus, delivery, user);
        int   totalPrice = 0;

        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice(); // 각 상품의 총 가격 계산
            order.addOrderItem(orderItem);
        }

        order.setTotalOrderPrice(totalPrice); // 전체 주문 가격 설정
        return order;
    }

    private void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void updateOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
