package com.moviebooking.patterns.chain;

import com.moviebooking.entity.Showtime;
import com.moviebooking.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Request object passed through the validation chain.
 */
@Getter
@Builder
public class BookingValidationRequest {

    private final User user;
    private final Showtime showtime;
    private final List<Long> seatIds;
    private final List<Long> bookedSeatIds;
    private final List<Long> lockedSeatIds;
    private final long activeBookingsCount;
}
