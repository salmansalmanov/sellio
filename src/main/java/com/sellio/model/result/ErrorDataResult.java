package com.sellio.model.result;

public class ErrorDataResult<T> extends DataResult<T> {
    public ErrorDataResult(T data, String message, String code) {
        super(data, false, message, code);
    }
}
