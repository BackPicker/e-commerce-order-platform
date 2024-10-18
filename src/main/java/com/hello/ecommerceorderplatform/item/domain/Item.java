package com.hello.ecommerceorderplatform.item.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;      // 상품 ID

    @Column(nullable = false)
    private String itemName;    // 상품 이름

    @Column(nullable = false)
    private int price;   // 상품 가격

    @Column(nullable = false)
    private int quantity;    // 상품 수량

    @CreatedDate
    private LocalDateTime createdAt;    // 수량 등록일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishList_id")
    private WishList wishList; // ManyToOne 관계

}
