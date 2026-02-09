package com.moviebooking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AdminShowtimeRequest {

    @NotNull
    private Long movieId;

    @NotNull
    private Long screenId;

    @NotNull
    private LocalDate showDate;

    @NotNull
    private LocalTime showTime;

    @NotNull
    private BigDecimal basePrice;

    private String pricingStrategy;
}
