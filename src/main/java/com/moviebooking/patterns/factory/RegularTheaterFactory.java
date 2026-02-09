package com.moviebooking.patterns.factory;

import com.moviebooking.entity.Screen;
import com.moviebooking.entity.Seat;
import com.moviebooking.entity.Theater;
import com.moviebooking.entity.enums.ScreenType;
import com.moviebooking.entity.enums.SeatType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Factory for Regular theaters.
 * Standard screens with Dolby 5.1, normal seat layout.
 */
public class RegularTheaterFactory implements TheaterFactory {

    private static final int ROWS = 10;
    private static final int COLUMNS = 15;

    @Override
    public Screen createScreen(Theater theater, int screenNumber, String screenName) {
        Screen screen = new Screen();
        screen.setTheater(theater);
        screen.setScreenNumber(screenNumber);
        screen.setScreenName(screenName != null ? screenName : "Screen " + screenNumber);
        screen.setScreenType(ScreenType.REGULAR);
        screen.setRows(ROWS);
        screen.setColumns(COLUMNS);
        screen.setTotalSeats(ROWS * COLUMNS);
        screen.setSoundSystem(getSoundSystem());
        screen.setScreenSize(getScreenSize());
        screen.setSpecialFeatures(getSpecialFeatures());
        screen.setIsActive(true);
        return screen;
    }

    @Override
    public List<Seat> createSeats(Screen screen, BigDecimal basePrice) {
        List<Seat> seats = new ArrayList<>();
        String[] rowLetters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

        for (int r = 0; r < ROWS; r++) {
            for (int c = 1; c <= COLUMNS; c++) {
                Seat seat = new Seat();
                seat.setScreen(screen);
                seat.setRowLetter(rowLetters[r]);
                seat.setSeatNumber(c);
                seat.setSeatLabel(rowLetters[r] + c);

                // First 2 rows: VIP (1.5x), Middle rows: PREMIUM (1.2x), Last 2 rows: REGULAR (1.0x)
                if (r < 2) {
                    seat.setSeatType(SeatType.VIP);
                    seat.setBasePrice(basePrice.multiply(new BigDecimal("1.50")));
                } else if (r >= ROWS - 2) {
                    seat.setSeatType(SeatType.REGULAR);
                    seat.setBasePrice(basePrice.multiply(new BigDecimal("0.80")));
                } else {
                    seat.setSeatType(SeatType.PREMIUM);
                    seat.setBasePrice(basePrice.multiply(new BigDecimal("1.20")));
                }

                seat.setIsAvailable(true);
                seat.setIsWheelchair(r == ROWS - 1 && (c == 1 || c == 2));
                seat.setIsAisle(c == 5 || c == 11);
                seats.add(seat);
            }
        }
        return seats;
    }

    @Override
    public BigDecimal getBaseTicketPrice() {
        return new BigDecimal("200.00");
    }

    @Override
    public String getSoundSystem() {
        return "Dolby Digital 5.1";
    }

    @Override
    public String getScreenSize() {
        return "40ft x 20ft";
    }

    @Override
    public String getSpecialFeatures() {
        return "Standard 2D/3D projection, Recliner seats in VIP rows";
    }
}
