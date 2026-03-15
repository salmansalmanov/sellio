package com.sellio.model.result;

import java.util.Map;

public class ErrorResult extends Result {
    public ErrorResult(String message) {
        super(false, message);
    }
}
