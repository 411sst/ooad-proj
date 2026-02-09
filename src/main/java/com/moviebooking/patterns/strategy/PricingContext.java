package com.moviebooking.patterns.strategy;

import com.moviebooking.entity.Showtime;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Context object passed to pricing strategies.
 */
@Getter
@Builder
public class PricingContext {

    private final Showtime showtime;
    private final BigDecimal basePrice;
    private final double occupancyRate;  // 0.0 to 1.0
    private final LocalDateTime showDateTime;
    private final boolean isWeekend;
    private final boolean isHoliday;
    private final int daysUntilShow;
}
