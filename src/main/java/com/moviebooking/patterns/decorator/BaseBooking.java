package com.moviebooking.patterns.decorator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Decorator Pattern - Concrete Component: Base booking with only ticket cost.
 * Owned by: Vaishnav
 */
public class BaseBooking implements BookingComponent {

    private final BigDecimal ticketCost;
    private final int seatCount;
    private final String movieTitle;

    public BaseBooking(BigDecimal ticketCost, int seatCount, String movieTitle) {
        this.ticketCost = ticketCost;
        this.seatCount = seatCount;
        this.movieTitle = movieTitle;
    }

    @Override
    public BigDecimal getCost() {
        return ticketCost;
    }

    @Override
    public String getDescription() {
        return String.format("%s - %d seat(s)", movieTitle, seatCount);
    }

    @Override
    public List<String> getItemDetails() {
        List<String> details = new ArrayList<>();
        details.add(String.format("Tickets (%d seats): ₹%s", seatCount, ticketCost));
        return details;
    }
}
