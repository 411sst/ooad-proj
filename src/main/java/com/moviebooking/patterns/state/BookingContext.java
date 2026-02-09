package com.moviebooking.patterns.state;

import com.moviebooking.entity.Booking;
import com.moviebooking.entity.enums.BookingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * State Pattern - Context class that manages booking state transitions.
 * Delegates behavior to the current state object.
 * Owned by: Shrish
 */
public class BookingContext {

    private static final Logger log = LoggerFactory.getLogger(BookingContext.class);

    private Booking booking;
    private BookingState currentState;
    private final List<Consumer<BookingContext>> transitionListeners = new ArrayList<>();

    public BookingContext(Booking booking) {
        this.booking = booking;
        this.currentState = resolveState(booking.getStatus());
    }

    public void setState(BookingState newState) {
        String oldStateName = currentState != null ? currentState.getStateName() : "NONE";
        this.currentState = newState;
        log.info("Booking {} transitioned: {} -> {}", booking.getBookingReference(), oldStateName, newState.getStateName());
        notifyListeners();
    }

    public void lock() {
        currentState.handleLock(this);
    }

    public void confirm() {
        currentState.handleConfirm(this);
    }

    public void cancel() {
        currentState.handleCancel(this);
    }

    public void refund() {
        currentState.handleRefund(this);
    }

    public void expire() {
        currentState.handleExpire(this);
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public BookingState getCurrentState() {
        return currentState;
    }

    public BookingStatus getStatus() {
        return booking.getStatus();
    }

    public void addTransitionListener(Consumer<BookingContext> listener) {
        transitionListeners.add(listener);
    }

    private void notifyListeners() {
        for (Consumer<BookingContext> listener : transitionListeners) {
            listener.accept(this);
        }
    }

    private BookingState resolveState(BookingStatus status) {
        if (status == null) return new PendingState();
        return switch (status) {
            case PENDING -> new PendingState();
            case LOCKED -> new LockedState();
            case CONFIRMED -> new ConfirmedState();
            case CANCELLED -> new CancelledState();
            case REFUNDED -> new RefundedState();
        };
    }
}
