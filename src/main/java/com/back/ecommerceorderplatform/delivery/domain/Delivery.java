package com.back.ecommerceorderplatform.delivery.domain;

import com.back.ecommerceorderplatform.order.domain.Order;
import com.back.ecommerceorderplatform.user.domain.Address;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    private Order order;   // OneToOne 관계

    @Column(nullable = false)
    @Embedded
    private Address address;    // 회원 주소

    public Delivery(Address address) {
        this.address = address;
    }
}