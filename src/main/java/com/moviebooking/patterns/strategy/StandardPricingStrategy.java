package com.moviebooking.patterns.strategy;

import java.math.BigDecimal;

/**
 * Standard pricing - no adjustments (multiplier = 1.0).
 */
public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculateMultiplier(PricingContext context) {
        return BigDecimal.ONE;
    }

    @Override
    public String getStrategyName() {
        return "STANDARD";
    }
}
