package com.sellio.model.result;

public class SuccessResult extends Result {
    public SuccessResult(String message, String code) {
        super(true, message);
    }
}
