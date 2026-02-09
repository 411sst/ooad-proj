package com.moviebooking.patterns.state;

import com.moviebooking.entity.Booking;

/**
 * State Pattern - BookingState Interface
 * Each state defines what operations are valid and handles transitions.
 * Owned by: Shrish
 */
public interface BookingState {

    void handleLock(BookingContext context);

    void handleConfirm(BookingContext context);

    void handleCancel(BookingContext context);

    void handleRefund(BookingContext context);

    void handleExpire(BookingContext context);

    String getStateName();

    String getStateDescription();
}
