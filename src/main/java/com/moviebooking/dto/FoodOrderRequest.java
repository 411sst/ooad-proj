package com.moviebooking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class FoodOrderRequest {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    // Map of foodItemId -> quantity
    @NotNull(message = "Items are required")
    private Map<Long, Integer> items;
}
