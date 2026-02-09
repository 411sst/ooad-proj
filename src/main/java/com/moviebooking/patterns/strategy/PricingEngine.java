package com.moviebooking.patterns.strategy;

import com.moviebooking.entity.Showtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Pricing Engine - Applies multiple pricing strategies and computes final price.
 * Combines all applicable strategy multipliers.
 */
@Component
public class PricingEngine {

    private static final Logger log = LoggerFactory.getLogger(PricingEngine.class);

    private final List<PricingStrategy> strategies;

    public PricingEngine() {
        this.strategies = new ArrayList<>();
        // Default strategies
        this.strategies.add(new PeakHourPricingStrategy());
        this.strategies.add(new WeekendPricingStrategy());
        this.strategies.add(new DemandBasedPricingStrategy());
    }

    /**
     * Calculate the final price for a showtime seat.
     * Applies all strategy multipliers cumulatively.
     */
    public BigDecimal calculateFinalPrice(BigDecimal basePrice, Showtime showtime) {
        double occupancy = 1.0 - ((double) showtime.getAvailableSeats() / showtime.getTotalSeats());
        LocalDateTime showDateTime = showtime.getShowDatetime();
        LocalDate showDate = showtime.getShowDate();
        boolean isWeekend = showDate.getDayOfWeek() == DayOfWeek.SATURDAY ||
                            showDate.getDayOfWeek() == DayOfWeek.SUNDAY;
        int daysUntilShow = (int) ChronoUnit.DAYS.between(LocalDate.now(), showDate);

        PricingContext context = PricingContext.builder()
                .showtime(showtime)
                .basePrice(basePrice)
                .occupancyRate(occupancy)
                .showDateTime(showDateTime)
                .isWeekend(isWeekend)
                .isHoliday(false) // Could be extended to check holiday calendar
                .daysUntilShow(daysUntilShow)
                .build();

        BigDecimal combinedMultiplier = BigDecimal.ONE;
        StringBuilder appliedStrategies = new StringBuilder();

        for (PricingStrategy strategy : strategies) {
            BigDecimal multiplier = strategy.calculateMultiplier(context);
            if (multiplier.compareTo(BigDecimal.ONE) != 0) {
                combinedMultiplier = combinedMultiplier.multiply(multiplier);
                appliedStrategies.append(strategy.getStrategyName())
                        .append("(x").append(multiplier).append(") ");
            }
        }

        BigDecimal finalPrice = basePrice.multiply(combinedMultiplier).setScale(2, RoundingMode.HALF_UP);

        if (appliedStrategies.length() > 0) {
            log.debug("Pricing for showtime {}: base={}, strategies=[{}], final={}",
                    showtime.getId(), basePrice, appliedStrategies.toString().trim(), finalPrice);
        }

        return finalPrice;
    }

    /**
     * Get the pricing breakdown (which strategies applied) for display.
     */
    public List<PricingBreakdown> getPricingBreakdown(BigDecimal basePrice, Showtime showtime) {
        double occupancy = 1.0 - ((double) showtime.getAvailableSeats() / showtime.getTotalSeats());
        LocalDateTime showDateTime = showtime.getShowDatetime();
        LocalDate showDate = showtime.getShowDate();
        boolean isWeekend = showDate.getDayOfWeek() == DayOfWeek.SATURDAY ||
                            showDate.getDayOfWeek() == DayOfWeek.SUNDAY;
        int daysUntilShow = (int) ChronoUnit.DAYS.between(LocalDate.now(), showDate);

        PricingContext context = PricingContext.builder()
                .showtime(showtime)
                .basePrice(basePrice)
                .occupancyRate(occupancy)
                .showDateTime(showDateTime)
                .isWeekend(isWeekend)
                .isHoliday(false)
                .daysUntilShow(daysUntilShow)
                .build();

        List<PricingBreakdown> breakdowns = new ArrayList<>();
        breakdowns.add(new PricingBreakdown("Base Price", BigDecimal.ONE, basePrice));

        BigDecimal runningPrice = basePrice;
        for (PricingStrategy strategy : strategies) {
            BigDecimal multiplier = strategy.calculateMultiplier(context);
            if (multiplier.compareTo(BigDecimal.ONE) != 0) {
                runningPrice = runningPrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
                breakdowns.add(new PricingBreakdown(strategy.getStrategyName(), multiplier, runningPrice));
            }
        }

        return breakdowns;
    }

    public record PricingBreakdown(String strategyName, BigDecimal multiplier, BigDecimal priceAfter) {}
}
