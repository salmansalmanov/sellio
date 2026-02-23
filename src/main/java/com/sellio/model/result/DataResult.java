package com.sellio.model.result;

import lombok.Getter;

@Getter
public class DataResult<T> extends Result {
    private final T data;

    public DataResult(T data, boolean success, String message, String code) {
        super(success, message, code);
        this.data = data;
    }
}
