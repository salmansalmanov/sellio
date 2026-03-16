package com.sellio.exception.custom;

public class ListingLimitException extends RuntimeException {
    public ListingLimitException(String message) {
        super(message);
    }
}
