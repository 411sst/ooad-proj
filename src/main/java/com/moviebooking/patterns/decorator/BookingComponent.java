package com.moviebooking.patterns.decorator;

import java.math.BigDecimal;
import java.util.List;

/**
 * Decorator Pattern - Component interface for booking cost calculation.
 * Owned by: Vaishnav
 */
public interface BookingComponent {

    BigDecimal getCost();

    String getDescription();

    List<String> getItemDetails();
}
