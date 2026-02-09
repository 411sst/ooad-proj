package com.moviebooking.service;

import com.moviebooking.dto.TheaterDto;
import com.moviebooking.entity.Screen;
import com.moviebooking.entity.Seat;
import com.moviebooking.entity.Theater;
import com.moviebooking.entity.enums.TheaterType;
import com.moviebooking.exception.ResourceNotFoundException;
import com.moviebooking.patterns.factory.TheaterFactory;
import com.moviebooking.patterns.factory.TheaterFactoryProvider;
import com.moviebooking.repository.ScreenRepository;
import com.moviebooking.repository.SeatRepository;
import com.moviebooking.repository.TheaterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TheaterService {

    private static final Logger log = LoggerFactory.getLogger(TheaterService.class);

    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final TheaterFactoryProvider factoryProvider;

    public TheaterService(TheaterRepository theaterRepository, ScreenRepository screenRepository,
                         SeatRepository seatRepository, TheaterFactoryProvider factoryProvider) {
        this.theaterRepository = theaterRepository;
        this.screenRepository = screenRepository;
        this.seatRepository = seatRepository;
        this.factoryProvider = factoryProvider;
    }

    public List<TheaterDto> getAllTheaters() {
        return theaterRepository.findAll().stream()
                .map(TheaterDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TheaterDto> getActiveTheaters() {
        return theaterRepository.findByIsActiveTrue().stream()
                .map(TheaterDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TheaterDto> getTheatersByCity(String city) {
        return theaterRepository.findByCityAndIsActiveTrue(city).stream()
                .map(TheaterDto::fromEntity)
                .collect(Collectors.toList());
    }

    public TheaterDto getTheaterById(Long id) {
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theater", "id", id));
        TheaterDto dto = TheaterDto.fromEntity(theater);

        // Add screen details
        List<Screen> screens = screenRepository.findByTheaterId(id);
        dto.setScreens(screens.stream().map(s -> {
            TheaterDto.ScreenDto sd = new TheaterDto.ScreenDto();
            sd.setId(s.getId());
            sd.setScreenNumber(s.getScreenNumber());
            sd.setScreenName(s.getScreenName());
            sd.setScreenType(s.getScreenType().name());
            sd.setTotalSeats(s.getTotalSeats());
            sd.setRows(s.getRows());
            sd.setColumns(s.getColumns());
            sd.setSoundSystem(s.getSoundSystem());
            sd.setScreenSize(s.getScreenSize());
            sd.setSpecialFeatures(s.getSpecialFeatures());
            sd.setIsActive(s.getIsActive());
            return sd;
        }).collect(Collectors.toList()));

        return dto;
    }

    /**
     * Create a new theater with screens and seats using Abstract Factory pattern.
     */
    @Transactional
    public TheaterDto createTheater(String name, String location, String city, String state,
                                    String pincode, TheaterType theaterType, int numScreens,
                                    String facilities) {
        Theater theater = new Theater();
        theater.setName(name);
        theater.setLocation(location);
        theater.setCity(city);
        theater.setState(state);
        theater.setPincode(pincode);
        theater.setTheaterType(theaterType);
        theater.setTotalScreens(numScreens);
        theater.setFacilities(facilities);
        theater.setIsActive(true);
        theater = theaterRepository.save(theater);

        // Use Abstract Factory to create screens and seats
        TheaterFactory factory = factoryProvider.getFactory(theaterType);
        BigDecimal basePrice = factory.getBaseTicketPrice();

        for (int i = 1; i <= numScreens; i++) {
            Screen screen = factory.createScreen(theater, i, null);
            screen = screenRepository.save(screen);

            List<Seat> seats = factory.createSeats(screen, basePrice);
            seatRepository.saveAll(seats);

            log.info("Created screen '{}' with {} seats for theater '{}'",
                    screen.getScreenName(), seats.size(), theater.getName());
        }

        return getTheaterById(theater.getId());
    }

    public List<Screen> getScreensForTheater(Long theaterId) {
        return screenRepository.findByTheaterIdAndIsActiveTrue(theaterId);
    }
}
