package com.hello.ecommerceorderplatform.order.domain;


import com.hello.ecommerceorderplatform.item.domain.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; // ManyToOne 관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;    // ManyToOne 관계

    @Column(nullable = false)
    private int totalPrice;   // 총 주문 가격

    @Column(nullable = false)
    private int orderCount;     // 주문 수량

    public OrderItem(Item item, int totalPrice, int orderCount) {
        this.item       = item;
        this.totalPrice = totalPrice;
        this.orderCount = orderCount;
    }

}
