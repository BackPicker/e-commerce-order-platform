package com.hello.ecommerceorderplatform.order.domain;

public enum OrderStatus {
    /**
     * 주문, 사용자에 의한 주문취소, 품절에 의한 주문취소
     */
    ORDER_START,
    ORDER_PAID,
    CANCELED_BY_USER,
    CANCELED_BY_SOLD_OUT
}
