package com.moviebooking.patterns.decorator;

import java.math.BigDecimal;
import java.util.List;

/**
 * Decorator Pattern - Abstract Decorator: Base class for all F&B decorators.
 * Wraps a BookingComponent and delegates to it.
 * Owned by: Vaishnav
 */
public abstract class BookingDecorator implements BookingComponent {

    protected final BookingComponent wrappedBooking;

    public BookingDecorator(BookingComponent wrappedBooking) {
        this.wrappedBooking = wrappedBooking;
    }

    @Override
    public BigDecimal getCost() {
        return wrappedBooking.getCost();
    }

    @Override
    public String getDescription() {
        return wrappedBooking.getDescription();
    }

    @Override
    public List<String> getItemDetails() {
        return wrappedBooking.getItemDetails();
    }
}
