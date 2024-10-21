package com.hello.ecommerceorderplatform.orderitem.domain;

public enum OrderStatus {
    /**
     * 주문, 사용자에 의한 주문취소, 품절에 의한 주문취소
     */
    ORDER, CANCELED_BY_USER, CANCELED_BY_SOLD_OUT
}
