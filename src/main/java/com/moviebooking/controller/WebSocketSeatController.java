package com.moviebooking.controller;

import com.moviebooking.patterns.observer.SeatUpdateEvent;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * WebSocket controller for real-time seat selection updates.
 * Clients subscribe to /topic/seats/{showtimeId} and send messages to /app/seats/{showtimeId}.
 */
@Controller
public class WebSocketSeatController {

    @MessageMapping("/seats/{showtimeId}")
    @SendTo("/topic/seats/{showtimeId}")
    public SeatUpdateEvent handleSeatUpdate(@DestinationVariable Long showtimeId, SeatUpdateEvent event) {
        event.setShowtimeId(showtimeId);
        return event;
    }
}
