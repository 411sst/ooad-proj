package com.moviebooking.patterns.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Observer Pattern - Concrete observer that broadcasts seat updates via WebSocket/STOMP.
 * All connected clients subscribed to /topic/seats/{showtimeId} receive real-time updates.
 * Owned by: Shrish
 */
@Component
public class WebSocketSeatObserver implements SeatObserver {

    private static final Logger log = LoggerFactory.getLogger(WebSocketSeatObserver.class);

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketSeatObserver(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void onSeatStatusChanged(SeatUpdateEvent event) {
        String destination = "/topic/seats/" + event.getShowtimeId();
        log.info("WebSocket broadcast to {}: {} seat(s) updated", destination, event.getUpdatedSeats().size());
        messagingTemplate.convertAndSend(destination, event);
    }
}
