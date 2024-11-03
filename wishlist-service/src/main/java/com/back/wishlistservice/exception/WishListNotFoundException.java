package com.back.wishlistservice.exception;

public class WishListNotFoundException extends RuntimeException {

    public WishListNotFoundException(String message) {
        super(message);
    }
}
