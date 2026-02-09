package com.moviebooking.patterns.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Observer Pattern - Subject that manages observers and broadcasts seat updates.
 * Uses CopyOnWriteArrayList for thread-safety with concurrent WebSocket clients.
 * Owned by: Shrish
 */
@Component
public class SeatAvailabilitySubject {

    private static final Logger log = LoggerFactory.getLogger(SeatAvailabilitySubject.class);

    private final List<SeatObserver> observers = new CopyOnWriteArrayList<>();

    public void addObserver(SeatObserver observer) {
        observers.add(observer);
        log.debug("Observer added. Total observers: {}", observers.size());
    }

    public void removeObserver(SeatObserver observer) {
        observers.remove(observer);
        log.debug("Observer removed. Total observers: {}", observers.size());
    }

    public void notifyObservers(SeatUpdateEvent event) {
        log.info("Broadcasting seat update for showtime {}: {} seats changed (type: {})",
                event.getShowtimeId(), event.getUpdatedSeats().size(), event.getEventType());
        for (SeatObserver observer : observers) {
            try {
                observer.onSeatStatusChanged(event);
            } catch (Exception e) {
                log.error("Error notifying observer: {}", e.getMessage());
            }
        }
    }

    public int getObserverCount() {
        return observers.size();
    }
}
