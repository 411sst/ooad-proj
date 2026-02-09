package com.moviebooking.patterns.chain;

/**
 * Validates that user hasn't exceeded concurrent booking limits.
 * Max 5 active (LOCKED/CONFIRMED) bookings at a time.
 */
public class BookingLimitHandler extends BookingValidationHandler {

    private static final int MAX_ACTIVE_BOOKINGS = 5;

    @Override
    protected ValidationResult validate(BookingValidationRequest request) {
        if (request.getActiveBookingsCount() >= MAX_ACTIVE_BOOKINGS) {
            return ValidationResult.failure(
                    "Maximum " + MAX_ACTIVE_BOOKINGS + " active bookings allowed. Please complete or cancel existing bookings first.",
                    getHandlerName()
            );
        }
        return ValidationResult.success();
    }

    @Override
    public String getHandlerName() {
        return "BookingLimit";
    }
}
