package com.back.itemservice.dto;


import com.back.itemservice.domain.Item;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemDetailResponseDto {


    private Long   itemId;
    private String itemName;
    private String category;
    private int    price;
    private int    quantity;

    @Builder
    public ItemDetailResponseDto(Long itemId,
                                 String itemName,
                                 String category,
                                 int price,
                                 int quantity) {
        this.itemId   = itemId;
        this.itemName = itemName;
        this.category = category;
        this.price    = price;
        this.quantity = quantity;
    }


    public static ItemDetailResponseDto entityFromDTO(Item item) {
        return ItemDetailResponseDto.builder()
                .itemId(item.getId())
                .itemName(item.getItemName())
                .category(item.getCategory())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .build();
    }
}