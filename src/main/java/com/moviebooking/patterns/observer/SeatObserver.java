package com.moviebooking.patterns.observer;

/**
 * Observer Pattern - Observer interface for seat availability updates.
 * Owned by: Shrish
 */
public interface SeatObserver {

    void onSeatStatusChanged(SeatUpdateEvent event);
}
