package com.moviebooking.patterns.chain;

/**
 * Chain of Responsibility Pattern - Booking validation pipeline.
 * Each handler validates one aspect and passes to the next.
 */
public abstract class BookingValidationHandler {

    protected BookingValidationHandler nextHandler;

    public BookingValidationHandler setNext(BookingValidationHandler next) {
        this.nextHandler = next;
        return next;
    }

    public ValidationResult handle(BookingValidationRequest request) {
        ValidationResult result = validate(request);
        if (!result.isValid()) {
            return result;
        }
        if (nextHandler != null) {
            return nextHandler.handle(request);
        }
        return ValidationResult.success();
    }

    protected abstract ValidationResult validate(BookingValidationRequest request);

    public abstract String getHandlerName();
}
