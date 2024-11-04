package com.back.orderservice.order.domain;

public enum OrderStatus {

    // 결제 상태
    PAYMENT_STATUS_COMPLETED, // 결제 완료

    // 주문 상태
    ORDER_CANCELLATION_IN_PROGRESS, // 주문 취소 중
    ORDER_CANCELED_BY_USER,        // 사용자에 의한 주문 취소
    ORDER_CANCELED_BY_SOLD_OUT,    // 품절에 의한 주문 취소

    // 배송 상태
    DELIVERY_IN_PROGRESS, // 배송 중
    DELIVERY_COMPLETED,   // 배송 완료


}
