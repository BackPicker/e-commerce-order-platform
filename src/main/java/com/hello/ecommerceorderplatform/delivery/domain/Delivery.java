package com.hello.ecommerceorderplatform.delivery.domain;

import com.hello.ecommerceorderplatform.order.domain.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Order order;   // OneToOne 관계

    @Column(nullable = false)
    private String address;    // 회원 주소

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus deliveryStatus;

    public Delivery(DeliveryStatus deliveryStatus, String address) {
        this.deliveryStatus = deliveryStatus;
        this.address        = address;
    }
}