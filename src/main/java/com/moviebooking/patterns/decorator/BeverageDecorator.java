package com.moviebooking.patterns.decorator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Decorator Pattern - Concrete Decorator: Adds beverage items to booking.
 * Owned by: Vaishnav
 */
public class BeverageDecorator extends BookingDecorator {

    private final String beverageName;
    private final BigDecimal beveragePrice;
    private final int quantity;

    public BeverageDecorator(BookingComponent wrappedBooking, String beverageName, BigDecimal beveragePrice, int quantity) {
        super(wrappedBooking);
        this.beverageName = beverageName;
        this.beveragePrice = beveragePrice;
        this.quantity = quantity;
    }

    @Override
    public BigDecimal getCost() {
        return super.getCost().add(beveragePrice.multiply(BigDecimal.valueOf(quantity)));
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + " + beverageName + " x" + quantity;
    }

    @Override
    public List<String> getItemDetails() {
        List<String> details = new ArrayList<>(super.getItemDetails());
        BigDecimal subtotal = beveragePrice.multiply(BigDecimal.valueOf(quantity));
        details.add(String.format("%s x%d: ₹%s", beverageName, quantity, subtotal));
        return details;
    }
}
