package com.moviebooking.patterns.state;

import com.moviebooking.entity.enums.BookingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * State Pattern - ConfirmedState: Payment successful, booking confirmed.
 * Valid transitions: CONFIRMED -> CANCELLED (if cancellation allowed)
 */
public class ConfirmedState implements BookingState {

    private static final Logger log = LoggerFactory.getLogger(ConfirmedState.class);

    @Override
    public void handleLock(BookingContext context) {
        throw new IllegalStateException("Cannot lock seats - booking already confirmed.");
    }

    @Override
    public void handleConfirm(BookingContext context) {
        log.warn("Booking {}: Already confirmed", context.getBooking().getBookingReference());
    }

    @Override
    public void handleCancel(BookingContext context) {
        log.info("Booking {}: Cancelling confirmed booking", context.getBooking().getBookingReference());
        context.getBooking().setStatus(BookingStatus.CANCELLED);
        context.getBooking().setCancelledDatetime(LocalDateTime.now());
        context.setState(new CancelledState());
    }

    @Override
    public void handleRefund(BookingContext context) {
        throw new IllegalStateException("Must cancel booking before processing refund.");
    }

    @Override
    public void handleExpire(BookingContext context) {
        log.warn("Booking {}: Confirmed bookings do not expire", context.getBooking().getBookingReference());
    }

    @Override
    public String getStateName() {
        return "CONFIRMED";
    }

    @Override
    public String getStateDescription() {
        return "Payment completed, seats permanently booked, QR code generated";
    }
}
