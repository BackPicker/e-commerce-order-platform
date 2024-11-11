package com.back.orderservice.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateOrderDTO {

    private Long    itemId;
    @NotNull
    private Integer orderCount;
    @NotNull
    private long payment;
}
