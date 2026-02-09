package com.moviebooking.patterns.strategy;

import com.moviebooking.patterns.singleton.AppConfigManager;

import java.math.BigDecimal;

/**
 * Demand-based pricing - adjusts price based on occupancy rate.
 * High demand (>70% occupied) = price increase.
 * Low demand (<30% occupied) = price decrease.
 */
public class DemandBasedPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculateMultiplier(PricingContext context) {
        AppConfigManager config = AppConfigManager.getInstance();
        double occupancy = context.getOccupancyRate();
        double highThreshold = config.getBigDecimal("pricing.high.demand.threshold").doubleValue();

        if (occupancy >= highThreshold) {
            // High demand: 25% increase
            return config.getBigDecimal("pricing.high.demand.multiplier");
        } else if (occupancy < 0.30) {
            // Low demand: 10% discount
            return config.getBigDecimal("pricing.low.demand.multiplier");
        }

        return BigDecimal.ONE;
    }

    @Override
    public String getStrategyName() {
        return "DEMAND_BASED";
    }
}
