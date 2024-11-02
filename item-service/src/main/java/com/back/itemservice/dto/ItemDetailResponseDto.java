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
    private String description;

    @Builder
    public ItemDetailResponseDto(String itemName,
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

    public static ItemDetailResponseDto entityFromDTO(Item item) {
        return ItemDetailResponseDto.builder()
                .itemName(item.getItemName())
                .category(item.getCategory())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .build();
    }
}