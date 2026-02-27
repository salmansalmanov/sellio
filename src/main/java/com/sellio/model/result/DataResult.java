package com.sellio.model.result;

import lombok.Getter;

@Getter
public class DataResult<T> extends Result {
    private final T data;

    public DataResult(T data, boolean success, String message) {
        super(success, message);
        this.data = data;
    }
}
