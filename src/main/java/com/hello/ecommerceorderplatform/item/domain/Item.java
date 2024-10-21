package com.hello.ecommerceorderplatform.item.domain;


import com.hello.ecommerceorderplatform.item.dto.ItemRequestDto;
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
    private String category;    // 상품 카테고리

    @Column(nullable = false)
    private int price;   // 상품 가격

    @Column(nullable = false)
    private int quantity;    // 상품 수량

    @Column(nullable = false)
    private String description; // 상품 설명

    @CreatedDate
    private final LocalDateTime createdAt = LocalDateTime.now();    // 수량 등록일


    public Item(String itemName, String category, int price, int quantity, String description) {
        this.itemName    = itemName;
        this.category    = category;
        this.price       = price;
        this.quantity    = quantity;
        this.description = description;
    }

    public Item(ItemRequestDto saveRequestDto) {
        this.itemName    = saveRequestDto.getItemName();
        this.category    = saveRequestDto.getCategory();
        this.price       = saveRequestDto.getPrice();
        this.quantity    = saveRequestDto.getQuantity();
        this.description = saveRequestDto.getDescription();
    }

    public void updateItemDetails(ItemRequestDto itemRequestDto) {
        this.itemName    = itemRequestDto.getItemName();
        this.category    = itemRequestDto.getCategory();
        this.price       = itemRequestDto.getPrice();
        this.quantity    = itemRequestDto.getQuantity();
        this.description = itemRequestDto.getDescription();
    }

    public void addItemQuantity(int quantity) {
        this.itemName += quantity;
    }
}
