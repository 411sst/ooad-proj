package com.moviebooking.patterns.observer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Observer Pattern - Event object broadcast to all observers when seat status changes.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatUpdateEvent {

    private Long showtimeId;
    private List<SeatStatusDto> updatedSeats;
    private String eventType; // LOCKED, BOOKED, RELEASED, EXPIRED
    private Long triggeredByUserId;
    private LocalDateTime timestamp;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatStatusDto {
        private Long seatId;
        private String seatLabel;
        private String status; // AVAILABLE, LOCKED, BOOKED
        private Long lockedByUserId;
    }
}
