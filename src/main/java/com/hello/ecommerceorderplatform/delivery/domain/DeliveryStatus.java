package com.hello.ecommerceorderplatform.delivery.domain;

public enum DeliveryStatus {
    // 결제 중, 결제 완료, 배달 중, 배달 완료, 예외에 의한 발송 중단
    PAYMENT_PROCESSING,
    PAYMENT_COMPLETED,
    IN_DELIVERY,
    DELIVERY_COMPLETED,
    DELIVERY_CANCELED_BY_ERROR
}
