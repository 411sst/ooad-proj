package com.moviebooking.patterns.chain;

/**
 * Validates that the user is active and eligible to make bookings.
 */
public class UserVerificationHandler extends BookingValidationHandler {

    @Override
    protected ValidationResult validate(BookingValidationRequest request) {
        if (request.getUser() == null) {
            return ValidationResult.failure("User not found. Please login again.", getHandlerName());
        }
        if (!request.getUser().getIsActive()) {
            return ValidationResult.failure("Your account is deactivated. Please contact support.", getHandlerName());
        }
        return ValidationResult.success();
    }

    @Override
    public String getHandlerName() {
        return "UserVerification";
    }
}
