package com.moviebooking.patterns.chain;

import org.springframework.stereotype.Component;

/**
 * Builds and provides the booking validation chain.
 * Chain order: UserVerification → ShowtimeValidation → SeatAvailability → BookingLimit
 */
@Component
public class BookingValidationChain {

    /**
     * Build the validation chain and run the request through it.
     */
    public ValidationResult validate(BookingValidationRequest request) {
        // Build chain
        BookingValidationHandler chain = new UserVerificationHandler();
        chain.setNext(new ShowtimeValidationHandler())
             .setNext(new SeatAvailabilityHandler())
             .setNext(new BookingLimitHandler());

        return chain.handle(request);
    }
}
