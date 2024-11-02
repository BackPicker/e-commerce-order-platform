package com.back.itemservice.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemSearchCondition {

    private String itemName;    // 상품 이름

    private Integer itemQuantityLoe; // 상품 수량이 n보다 작은 것

    private Integer itemPriceLoe;    // 상품 가격이 n보다 싼 것

    private Integer itemPriceGoe;    // 상품 가격이 n보다 비싼 것

    public ItemSearchCondition(String itemName,
                               Integer itemQuantityLoe,
                               Integer itemPriceLoe,
                               Integer itemPriceGoe) {
        this.itemName        = itemName;
        this.itemQuantityLoe = itemQuantityLoe;
        this.itemPriceLoe    = itemPriceLoe;
        this.itemPriceGoe    = itemPriceGoe;
    }
}
