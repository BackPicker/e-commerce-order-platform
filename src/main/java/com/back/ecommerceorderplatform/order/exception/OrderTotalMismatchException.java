package com.back.ecommerceorderplatform.order.exception;

public class OrderTotalMismatchException extends RuntimeException {

    public OrderTotalMismatchException(String message) {
        super(message);
    }
}
