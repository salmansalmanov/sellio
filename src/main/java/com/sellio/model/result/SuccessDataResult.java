package com.sellio.model.result;

public class SuccessDataResult<T> extends DataResult<T> {
    public SuccessDataResult(T data, String message, String code) {
        super(data, true, message, code);
    }
}
