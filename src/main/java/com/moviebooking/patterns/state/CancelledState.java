package com.moviebooking.patterns.state;

import com.moviebooking.entity.enums.BookingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * State Pattern - CancelledState: Booking has been cancelled.
 * Valid transitions: CANCELLED -> REFUNDED
 */
public class CancelledState implements BookingState {

    private static final Logger log = LoggerFactory.getLogger(CancelledState.class);

    @Override
    public void handleLock(BookingContext context) {
        throw new IllegalStateException("Cannot lock seats - booking is cancelled.");
    }

    @Override
    public void handleConfirm(BookingContext context) {
        throw new IllegalStateException("Cannot confirm - booking is cancelled.");
    }

    @Override
    public void handleCancel(BookingContext context) {
        log.warn("Booking {}: Already cancelled", context.getBooking().getBookingReference());
    }

    @Override
    public void handleRefund(BookingContext context) {
        log.info("Booking {}: Processing refund", context.getBooking().getBookingReference());
        context.getBooking().setStatus(BookingStatus.REFUNDED);
        context.setState(new RefundedState());
    }

    @Override
    public void handleExpire(BookingContext context) {
        log.warn("Booking {}: Cancelled bookings do not expire", context.getBooking().getBookingReference());
    }

    @Override
    public String getStateName() {
        return "CANCELLED";
    }

    @Override
    public String getStateDescription() {
        return "Booking cancelled, seats released, refund may be pending";
    }
}
