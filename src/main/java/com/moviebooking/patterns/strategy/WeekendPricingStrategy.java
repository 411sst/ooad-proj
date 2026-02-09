package com.moviebooking.patterns.strategy;

import com.moviebooking.patterns.singleton.AppConfigManager;

import java.math.BigDecimal;

/**
 * Weekend pricing - applies surcharge on weekends, holiday surcharge on holidays.
 */
public class WeekendPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculateMultiplier(PricingContext context) {
        AppConfigManager config = AppConfigManager.getInstance();

        if (context.isHoliday()) {
            return config.getBigDecimal("pricing.holiday.multiplier");
        } else if (context.isWeekend()) {
            return config.getBigDecimal("pricing.weekend.multiplier");
        }

        return BigDecimal.ONE;
    }

    @Override
    public String getStrategyName() {
        return "WEEKEND";
    }
}
