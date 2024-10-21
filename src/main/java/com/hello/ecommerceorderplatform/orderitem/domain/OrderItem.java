package com.hello.ecommerceorderplatform.orderitem.domain;


import com.hello.ecommerceorderplatform.item.domain.Item;
import com.hello.ecommerceorderplatform.order.domain.Order;
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

    // 주문 아이템 생성 메서드
    public static OrderItem createOrderItem(Item item, int orderCount) {
        if (orderCount <= 0) {
            throw new IllegalArgumentException("주문 수량은 1 이상이어야 합니다.");
        }
        int       totalPrice = item.getPrice() * orderCount;
        OrderItem orderItem  = new OrderItem(item, totalPrice, orderCount);
        item.removeQuantity(orderCount);
        return orderItem;
    }
}
