package com.back.orderservice.order.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long itemId;

    @Column(nullable = false)
    private Integer orderCount;     // 주문 수량

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;    // ManyToOne 관계

}
