package com.back.ecommerceorderplatform.item.domain;


import com.back.ecommerceorderplatform.common.entity.BaseEntity;
import com.back.ecommerceorderplatform.item.dto.ItemRequestDto;
import com.back.ecommerceorderplatform.item.exception.NosuchQuantityException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long   id;      // 상품 ID
    @Column(nullable = false)
    private String itemName;    // 상품 이름
    @Column(nullable = false)
    private String category;    // 상품 카테고리
    @Column(nullable = false)
    private int    price;   // 상품 가격
    @Column(nullable = false)
    private int    quantity;    // 상품 수량
    @Column(nullable = false)
    private String description; // 상품 설명


    public Item(String itemName,
                String category,
                int price,
                int quantity,
                String description) {
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

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void reduceQuantity(int orderCount) {
        int totalCount = this.quantity - orderCount;
        if (totalCount < 0) {
            throw new NosuchQuantityException("재고가 부족합니다");
        }
        this.quantity = totalCount;

    }
}
