package com.moviebooking.patterns.state;

import com.moviebooking.entity.enums.BookingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * State Pattern - LockedState: Seats temporarily reserved for 10 minutes.
 * Valid transitions: LOCKED -> CONFIRMED, LOCKED -> PENDING (expire), LOCKED -> CANCELLED
 */
public class LockedState implements BookingState {

    private static final Logger log = LoggerFactory.getLogger(LockedState.class);

    @Override
    public void handleLock(BookingContext context) {
        log.warn("Booking {}: Already in LOCKED state", context.getBooking().getBookingReference());
    }

    @Override
    public void handleConfirm(BookingContext context) {
        log.info("Booking {}: Payment successful, confirming booking", context.getBooking().getBookingReference());
        context.getBooking().setStatus(BookingStatus.CONFIRMED);
        context.getBooking().setConfirmedDatetime(LocalDateTime.now());
        context.setState(new ConfirmedState());
    }

    @Override
    public void handleCancel(BookingContext context) {
        log.info("Booking {}: Cancelled during payment, releasing seat locks", context.getBooking().getBookingReference());
        context.getBooking().setStatus(BookingStatus.CANCELLED);
        context.getBooking().setCancelledDatetime(LocalDateTime.now());
        context.setState(new CancelledState());
    }

    @Override
    public void handleRefund(BookingContext context) {
        throw new IllegalStateException("Cannot refund from LOCKED state - no payment completed yet.");
    }

    @Override
    public void handleExpire(BookingContext context) {
        log.info("Booking {}: Lock expired after 10 minutes, returning to PENDING", context.getBooking().getBookingReference());
        context.getBooking().setStatus(BookingStatus.PENDING);
        context.setState(new PendingState());
    }

    @Override
    public String getStateName() {
        return "LOCKED";
    }

    @Override
    public String getStateDescription() {
        return "Seats temporarily reserved, awaiting payment (10-minute window)";
    }
}
