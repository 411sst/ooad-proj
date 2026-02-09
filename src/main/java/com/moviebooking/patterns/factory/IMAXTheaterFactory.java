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
 * Concrete Factory for IMAX theaters.
 * Larger screens, Dolby Atmos, premium seating with recliners.
 */
public class IMAXTheaterFactory implements TheaterFactory {

    private static final int ROWS = 12;
    private static final int COLUMNS = 20;

    @Override
    public Screen createScreen(Theater theater, int screenNumber, String screenName) {
        Screen screen = new Screen();
        screen.setTheater(theater);
        screen.setScreenNumber(screenNumber);
        screen.setScreenName(screenName != null ? screenName : "IMAX " + screenNumber);
        screen.setScreenType(ScreenType.IMAX);
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
        String[] rowLetters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"};

        for (int r = 0; r < ROWS; r++) {
            for (int c = 1; c <= COLUMNS; c++) {
                Seat seat = new Seat();
                seat.setScreen(screen);
                seat.setRowLetter(rowLetters[r]);
                seat.setSeatNumber(c);
                seat.setSeatLabel(rowLetters[r] + c);

                // First 3 rows: RECLINER (2.0x), Next 3: VIP (1.5x), Middle: PREMIUM (1.3x), Last 3: REGULAR
                if (r < 3) {
                    seat.setSeatType(SeatType.RECLINER);
                    seat.setBasePrice(basePrice.multiply(new BigDecimal("2.00")));
                } else if (r < 6) {
                    seat.setSeatType(SeatType.VIP);
                    seat.setBasePrice(basePrice.multiply(new BigDecimal("1.50")));
                } else if (r < ROWS - 3) {
                    seat.setSeatType(SeatType.PREMIUM);
                    seat.setBasePrice(basePrice.multiply(new BigDecimal("1.30")));
                } else {
                    seat.setSeatType(SeatType.REGULAR);
                    seat.setBasePrice(basePrice);
                }

                seat.setIsAvailable(true);
                seat.setIsWheelchair(r == ROWS - 1 && (c == 1 || c == 2));
                seat.setIsAisle(c == 7 || c == 14);
                seats.add(seat);
            }
        }
        return seats;
    }

    @Override
    public BigDecimal getBaseTicketPrice() {
        return new BigDecimal("450.00");
    }

    @Override
    public String getSoundSystem() {
        return "Dolby Atmos 12.1";
    }

    @Override
    public String getScreenSize() {
        return "72ft x 53ft (IMAX Laser)";
    }

    @Override
    public String getSpecialFeatures() {
        return "IMAX Laser 4K projection, Dual projector system, 12-channel immersive audio, Recliner seats";
    }
}
