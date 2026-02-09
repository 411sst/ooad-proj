package com.moviebooking.patterns.chain;

import com.moviebooking.patterns.singleton.AppConfigManager;

import java.util.List;

/**
 * Validates seat availability - checks if seats are not already booked or locked.
 */
public class SeatAvailabilityHandler extends BookingValidationHandler {

    @Override
    protected ValidationResult validate(BookingValidationRequest request) {
        List<Long> seatIds = request.getSeatIds();
        List<Long> bookedSeats = request.getBookedSeatIds();
        List<Long> lockedSeats = request.getLockedSeatIds();

        if (seatIds == null || seatIds.isEmpty()) {
            return ValidationResult.failure("No seats selected.", getHandlerName());
        }

        int maxSeats = AppConfigManager.getInstance().getInt("seat.max.per.booking");
        if (seatIds.size() > maxSeats) {
            return ValidationResult.failure("Maximum " + maxSeats + " seats per booking.", getHandlerName());
        }

        // Check if any selected seats are already booked
        for (Long seatId : seatIds) {
            if (bookedSeats != null && bookedSeats.contains(seatId)) {
                return ValidationResult.failure("One or more selected seats are already booked.", getHandlerName());
            }
            if (lockedSeats != null && lockedSeats.contains(seatId)) {
                return ValidationResult.failure("One or more selected seats are temporarily locked by another user.", getHandlerName());
            }
        }

        return ValidationResult.success();
    }

    @Override
    public String getHandlerName() {
        return "SeatAvailability";
    }
}
