package com.back.itemservice.exception;

public class NosuchQuantityException extends RuntimeException {

    public NosuchQuantityException(String message) {
        super(message);
    }
}
