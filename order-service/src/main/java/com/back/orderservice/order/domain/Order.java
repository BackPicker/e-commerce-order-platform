package com.back.orderservice.order.domain;

import com.back.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long itemId;

    @Column(nullable = false)
    private Integer orderCount;

    @Column(nullable = false)
    private Long totalOrderPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public Order(Long userId,
                 Long itemId,
                 Integer orderCount,
                 Long totalOrderPrice) {
        this.userId          = userId;
        this.itemId          = itemId;
        this.orderCount      = orderCount;
        this.totalOrderPrice = totalOrderPrice;
        this.orderStatus     = OrderStatus.PAYMENT_STATUS_COMPLETED;
    }

    public void updateOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
