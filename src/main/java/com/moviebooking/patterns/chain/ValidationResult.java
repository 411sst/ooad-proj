package com.moviebooking.patterns.chain;

import lombok.Getter;

@Getter
public class ValidationResult {

    private final boolean valid;
    private final String message;
    private final String failedHandler;

    private ValidationResult(boolean valid, String message, String failedHandler) {
        this.valid = valid;
        this.message = message;
        this.failedHandler = failedHandler;
    }

    public static ValidationResult success() {
        return new ValidationResult(true, "Validation passed", null);
    }

    public static ValidationResult failure(String message, String handlerName) {
        return new ValidationResult(false, message, handlerName);
    }
}
