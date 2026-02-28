package com.sellio.exception.custom;

public class MailException extends RuntimeException {
    public MailException(String message) {
        super(message);
    }
}
