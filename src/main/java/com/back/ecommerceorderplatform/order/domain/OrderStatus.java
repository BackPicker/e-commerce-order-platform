package com.back.ecommerceorderplatform.order.domain;

public enum OrderStatus {


    PAYMENT_COMPLETED,    // 주문 완료

    // 주문 상태
    ORDER_CANCELED_BY_USER,  // 사용자에 의한 주문 취소
    ORDER_CANCELED_BY_SOLD_OUT, // 품절에 의한 주문 취소

    // 배송 상태
    IN_DELIVERY,                // 배송중
    DELIVERY_COMPLETED,        // 배송 완료
    DELIVERY_CANCELED_BY_USER, // 사용자에 의한 배송 취소
    DELIVERY_CANCELED;         // 품절 또는 기타 사유로 인한 배송 취소
}
