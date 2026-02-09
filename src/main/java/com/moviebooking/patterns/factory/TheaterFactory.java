package com.moviebooking.patterns.factory;

import com.moviebooking.entity.Screen;
import com.moviebooking.entity.Seat;
import com.moviebooking.entity.Theater;

import java.math.BigDecimal;
import java.util.List;

/**
 * Abstract Factory Pattern - Creates theater-specific components.
 * Each theater type (Regular, IMAX, 4DX) produces different screen configs,
 * seat layouts, and pricing structures.
 */
public interface TheaterFactory {

    Screen createScreen(Theater theater, int screenNumber, String screenName);

    List<Seat> createSeats(Screen screen, BigDecimal basePrice);

    BigDecimal getBaseTicketPrice();

    String getSoundSystem();

    String getScreenSize();

    String getSpecialFeatures();
}
