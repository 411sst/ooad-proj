package com.moviebooking.patterns.strategy;

import com.moviebooking.patterns.singleton.AppConfigManager;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * Peak hour pricing - applies surcharge for evening shows, discount for morning shows.
 */
public class PeakHourPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculateMultiplier(PricingContext context) {
        AppConfigManager config = AppConfigManager.getInstance();
        LocalTime showTime = context.getShowDateTime().toLocalTime();

        LocalTime peakStart = config.getTime("pricing.peak.start");
        LocalTime peakEnd = config.getTime("pricing.peak.end");
        LocalTime morningStart = config.getTime("pricing.morning.start");
        LocalTime morningEnd = config.getTime("pricing.morning.end");

        if (!showTime.isBefore(peakStart) && !showTime.isAfter(peakEnd)) {
            // Evening peak hours: 30% surcharge
            return config.getBigDecimal("pricing.peak.multiplier");
        } else if (!showTime.isBefore(morningStart) && !showTime.isAfter(morningEnd)) {
            // Morning shows: 20% discount
            return config.getBigDecimal("pricing.morning.discount");
        }

        return BigDecimal.ONE;
    }

    @Override
    public String getStrategyName() {
        return "PEAK_HOUR";
    }
}
