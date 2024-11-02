package com.back.itemservice.dto;


import com.back.itemservice.domain.Item;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemDetailResponseDto {

    private String itemName;
    private String category;
    private int    price;
    private int    quantity;

    @Builder
    public ItemDetailResponseDto(String itemName,
                                 String category,
                                 int price,
                                 int quantity) {
        this.itemName    = itemName;
        this.category    = category;
        this.price       = price;
        this.quantity    = quantity;
    }

    public static ItemDetailResponseDto entityFromDTO(Item item) {
        return ItemDetailResponseDto.builder()
                .itemName(item.getItemName())
                .category(item.getCategory())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .build();
    }
}