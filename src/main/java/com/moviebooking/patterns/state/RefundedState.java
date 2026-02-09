package com.moviebooking.patterns.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * State Pattern - RefundedState: Refund processed, terminal state.
 * No further transitions allowed.
 */
public class RefundedState implements BookingState {

    private static final Logger log = LoggerFactory.getLogger(RefundedState.class);

    @Override
    public void handleLock(BookingContext context) {
        throw new IllegalStateException("Cannot lock seats - booking is refunded (terminal state).");
    }

    @Override
    public void handleConfirm(BookingContext context) {
        throw new IllegalStateException("Cannot confirm - booking is refunded (terminal state).");
    }

    @Override
    public void handleCancel(BookingContext context) {
        throw new IllegalStateException("Cannot cancel - booking is already refunded (terminal state).");
    }

    @Override
    public void handleRefund(BookingContext context) {
        log.warn("Booking {}: Already refunded", context.getBooking().getBookingReference());
    }

    @Override
    public void handleExpire(BookingContext context) {
        log.warn("Booking {}: Refunded bookings do not expire", context.getBooking().getBookingReference());
    }

    @Override
    public String getStateName() {
        return "REFUNDED";
    }

    @Override
    public String getStateDescription() {
        return "Refund processed to original payment method, booking archived";
    }
}
