package com.back.orderservice.order.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateOrderDTO {

    private Long    itemId;
    private Integer orderCount;

    private long payment;
}
