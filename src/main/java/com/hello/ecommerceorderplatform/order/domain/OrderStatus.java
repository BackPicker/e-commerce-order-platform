package com.hello.ecommerceorderplatform.order.domain;

public enum OrderStatus {


    //  주문 완료
    PAYMENT_COMPLETED,

    //사용자에 의한 취소, 품절에 의한 취소
    CANCELED_BY_USER,
    CANCELED_BY_SOLD_OUT,

    // 배송중, 배송 완료, 유저에 의한 배송 취소, 반품 완료
    IN_DELIVERY,
    DELIVERY_COMPLETED,
    DELIVERY_CANCELED_BY_USER,
    DELIVERY_CANCELED
}
