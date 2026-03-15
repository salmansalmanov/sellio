package com.sellio.model.result;

import lombok.Getter;

@Getter
public abstract class Result {
    private final Boolean success;
    private final String message;

    public Result(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
