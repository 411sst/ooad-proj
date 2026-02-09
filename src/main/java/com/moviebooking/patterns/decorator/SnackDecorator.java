package com.moviebooking.patterns.decorator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Decorator Pattern - Concrete Decorator: Adds snack items to booking.
 * Owned by: Vaishnav
 */
public class SnackDecorator extends BookingDecorator {

    private final String snackName;
    private final BigDecimal snackPrice;
    private final int quantity;

    public SnackDecorator(BookingComponent wrappedBooking, String snackName, BigDecimal snackPrice, int quantity) {
        super(wrappedBooking);
        this.snackName = snackName;
        this.snackPrice = snackPrice;
        this.quantity = quantity;
    }

    @Override
    public BigDecimal getCost() {
        return super.getCost().add(snackPrice.multiply(BigDecimal.valueOf(quantity)));
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + " + snackName + " x" + quantity;
    }

    @Override
    public List<String> getItemDetails() {
        List<String> details = new ArrayList<>(super.getItemDetails());
        BigDecimal subtotal = snackPrice.multiply(BigDecimal.valueOf(quantity));
        details.add(String.format("%s x%d: ₹%s", snackName, quantity, subtotal));
        return details;
    }
}
