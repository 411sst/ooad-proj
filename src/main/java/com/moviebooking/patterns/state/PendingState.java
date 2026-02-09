package com.moviebooking.patterns.state;

import com.moviebooking.entity.enums.BookingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * State Pattern - PendingState: Initial state when seats are selected.
 * Valid transitions: PENDING -> LOCKED, PENDING -> CANCELLED
 */
public class PendingState implements BookingState {

    private static final Logger log = LoggerFactory.getLogger(PendingState.class);

    @Override
    public void handleLock(BookingContext context) {
        log.info("Booking {}: Locking seats for 10 minutes", context.getBooking().getBookingReference());
        context.getBooking().setStatus(BookingStatus.LOCKED);
        context.setState(new LockedState());
    }

    @Override
    public void handleConfirm(BookingContext context) {
        throw new IllegalStateException("Cannot confirm booking directly from PENDING state. Must lock seats first.");
    }

    @Override
    public void handleCancel(BookingContext context) {
        log.info("Booking {}: Cancelled from PENDING state", context.getBooking().getBookingReference());
        context.getBooking().setStatus(BookingStatus.CANCELLED);
        context.getBooking().setCancelledDatetime(java.time.LocalDateTime.now());
        context.setState(new CancelledState());
    }

    @Override
    public void handleRefund(BookingContext context) {
        throw new IllegalStateException("Cannot refund from PENDING state - no payment made.");
    }

    @Override
    public void handleExpire(BookingContext context) {
        log.info("Booking {}: Expired from PENDING state, cancelling", context.getBooking().getBookingReference());
        handleCancel(context);
    }

    @Override
    public String getStateName() {
        return "PENDING";
    }

    @Override
    public String getStateDescription() {
        return "Booking created, seats selected but not yet locked";
    }
}
