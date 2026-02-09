package com.moviebooking.patterns.factory;

import com.moviebooking.entity.enums.TheaterType;
import org.springframework.stereotype.Component;

/**
 * Factory Provider - Returns the appropriate factory based on theater type.
 * Acts as the Abstract Factory entry point.
 */
@Component
public class TheaterFactoryProvider {

    public TheaterFactory getFactory(TheaterType theaterType) {
        return switch (theaterType) {
            case IMAX -> new IMAXTheaterFactory();
            case FOUR_DX -> new FourDXTheaterFactory();
            default -> new RegularTheaterFactory();
        };
    }
}
