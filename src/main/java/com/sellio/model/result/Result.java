package com.sellio.model.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class Result {
    private final Boolean success;
    private final String message;

    @JsonIgnore
    private HttpStatus httpStatus;

    public Result(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
