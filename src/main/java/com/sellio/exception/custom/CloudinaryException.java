package com.sellio.exception.custom;

public class CloudinaryException extends RuntimeException {
    public CloudinaryException(String message) {
        super(message);
    }
}
