package com.back.orderservice.order.dto;

import com.back.common.dto.order.CreateOrderReqDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateOrderDTO {
    private Integer                 payment;
}
