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
 * Concrete Factory for 4DX theaters.
 * Motion seats, environmental effects, smaller capacity, premium pricing.
 */
public class FourDXTheaterFactory implements TheaterFactory {

    private static final int ROWS = 8;
    private static final int COLUMNS = 12;

    @Override
    public Screen createScreen(Theater theater, int screenNumber, String screenName) {
        Screen screen = new Screen();
        screen.setTheater(theater);
        screen.setScreenNumber(screenNumber);
        screen.setScreenName(screenName != null ? screenName : "4DX " + screenNumber);
        screen.setScreenType(ScreenType.FOUR_DX);
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
        String[] rowLetters = {"A", "B", "C", "D", "E", "F", "G", "H"};

        for (int r = 0; r < ROWS; r++) {
            for (int c = 1; c <= COLUMNS; c++) {
                Seat seat = new Seat();
                seat.setScreen(screen);
                seat.setRowLetter(rowLetters[r]);
                seat.setSeatNumber(c);
                seat.setSeatLabel(rowLetters[r] + c);

                // All 4DX seats are MOTION type, first 2 rows premium pricing
                if (r < 2) {
                    seat.setSeatType(SeatType.MOTION);
                    seat.setBasePrice(basePrice.multiply(new BigDecimal("1.80")));
                } else if (r < 5) {
                    seat.setSeatType(SeatType.MOTION);
                    seat.setBasePrice(basePrice.multiply(new BigDecimal("1.50")));
                } else {
                    seat.setSeatType(SeatType.MOTION);
                    seat.setBasePrice(basePrice);
                }

                seat.setIsAvailable(true);
                seat.setIsWheelchair(false); // 4DX not wheelchair accessible
                seat.setIsAisle(c == 4 || c == 9);
                seats.add(seat);
            }
        }
        return seats;
    }

    @Override
    public BigDecimal getBaseTicketPrice() {
        return new BigDecimal("600.00");
    }

    @Override
    public String getSoundSystem() {
        return "Dolby Atmos 7.1 + Environmental Audio";
    }

    @Override
    public String getScreenSize() {
        return "52ft x 28ft";
    }

    @Override
    public String getSpecialFeatures() {
        return "Motion seats, Wind, Water spray, Fog, Scent, Strobe lighting, Vibration effects";
    }
}
