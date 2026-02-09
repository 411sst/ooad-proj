package com.moviebooking.patterns.decorator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Decorator Pattern - Concrete Decorator: Adds combo deals with discounted pricing.
 * Owned by: Vaishnav
 */
public class ComboDecorator extends BookingDecorator {

    private final String comboName;
    private final BigDecimal comboPrice;
    private final int quantity;

    public ComboDecorator(BookingComponent wrappedBooking, String comboName, BigDecimal comboPrice, int quantity) {
        super(wrappedBooking);
        this.comboName = comboName;
        this.comboPrice = comboPrice;
        this.quantity = quantity;
    }

    @Override
    public BigDecimal getCost() {
        return super.getCost().add(comboPrice.multiply(BigDecimal.valueOf(quantity)));
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + " + comboName + " x" + quantity;
    }

    @Override
    public List<String> getItemDetails() {
        List<String> details = new ArrayList<>(super.getItemDetails());
        BigDecimal subtotal = comboPrice.multiply(BigDecimal.valueOf(quantity));
        details.add(String.format("%s (Combo) x%d: ₹%s", comboName, quantity, subtotal));
        return details;
    }
}
