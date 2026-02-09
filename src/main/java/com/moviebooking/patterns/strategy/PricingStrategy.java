package com.moviebooking.patterns.strategy;

import com.moviebooking.entity.Showtime;

import java.math.BigDecimal;

/**
 * Strategy Pattern - Dynamic pricing interface.
 * Different strategies calculate price multipliers based on various factors.
 */
public interface PricingStrategy {

    /**
     * Calculate the price multiplier for a given showtime context.
     * @param context the pricing context with showtime and occupancy info
     * @return the multiplier to apply (e.g., 1.0 = no change, 1.3 = 30% more)
     */
    BigDecimal calculateMultiplier(PricingContext context);

    String getStrategyName();
}
