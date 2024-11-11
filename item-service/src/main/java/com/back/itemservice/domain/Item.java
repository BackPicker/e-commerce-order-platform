package com.back.itemservice.domain;


import com.back.common.entity.BaseEntity;
import com.back.itemservice.dto.ItemRequestDto;
import com.back.itemservice.exception.NosuchQuantityException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long   id;
    private String itemName;
    private String category;
    private int    price;
    private int    quantity;

    @Builder
    public Item(String itemName,
                String category,
                int price,
                int quantity) {
        this.itemName = itemName;
        this.category = category;
        this.price    = price;
        this.quantity = quantity;
    }

    public static Item dtoToEntity(ItemRequestDto dto) {
        return Item.builder()
                .itemName(dto.getItemName())
                .category(dto.getCategory())
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .build();
    }

    public void updateItemDetails(ItemRequestDto itemRequestDto) {
        this.itemName = itemRequestDto.getItemName();
        this.category = itemRequestDto.getCategory();
        this.price    = itemRequestDto.getPrice();
        this.quantity = itemRequestDto.getQuantity();
    }

    public void reduceQuantity(int orderCount) {
        int totalCount = this.quantity - orderCount;
        if (totalCount < 0) {
            throw new NosuchQuantityException("재고가 부족합니다");
        }
        this.quantity = totalCount;
    }

    public void addQuantity(int orderCount) {
        this.quantity = this.quantity + orderCount;
    }

    public void updateQuantity(Integer redisQuantity) {
        this.quantity = redisQuantity;
    }
}
