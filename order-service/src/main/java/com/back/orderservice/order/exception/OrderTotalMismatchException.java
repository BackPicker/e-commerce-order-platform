package com.back.orderservice.order.exception;

public class OrderTotalMismatchException extends RuntimeException {

    public OrderTotalMismatchException(String message) {
        super(message);
    }
}
