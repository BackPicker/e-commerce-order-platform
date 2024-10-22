package com.hello.ecommerceorderplatform.order.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderRequestDto {

    private int    payment;
    private String paymentComplete = "주문 완료";


}
