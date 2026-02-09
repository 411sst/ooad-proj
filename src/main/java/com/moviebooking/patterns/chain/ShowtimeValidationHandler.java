package com.moviebooking.patterns.chain;

import com.moviebooking.entity.enums.ShowtimeStatus;

import java.time.LocalDateTime;

/**
 * Validates that the showtime is active and in the future.
 */
public class ShowtimeValidationHandler extends BookingValidationHandler {

    @Override
    protected ValidationResult validate(BookingValidationRequest request) {
        if (request.getShowtime() == null) {
            return ValidationResult.failure("Showtime not found.", getHandlerName());
        }
        if (request.getShowtime().getStatus() != ShowtimeStatus.ACTIVE) {
            return ValidationResult.failure("This showtime is no longer active.", getHandlerName());
        }
        if (request.getShowtime().getShowDatetime().isBefore(LocalDateTime.now())) {
            return ValidationResult.failure("Cannot book for a show that has already started.", getHandlerName());
        }
        if (request.getShowtime().getAvailableSeats() <= 0) {
            return ValidationResult.failure("No seats available for this showtime.", getHandlerName());
        }
        return ValidationResult.success();
    }

    @Override
    public String getHandlerName() {
        return "ShowtimeValidation";
    }
}
