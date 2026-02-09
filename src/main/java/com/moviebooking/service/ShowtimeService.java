package com.moviebooking.service;

import com.moviebooking.dto.ShowtimeDto;
import com.moviebooking.entity.Movie;
import com.moviebooking.entity.Screen;
import com.moviebooking.entity.Showtime;
import com.moviebooking.entity.enums.ShowtimeStatus;
import com.moviebooking.exception.BadRequestException;
import com.moviebooking.exception.ResourceNotFoundException;
import com.moviebooking.repository.MovieRepository;
import com.moviebooking.repository.ScreenRepository;
import com.moviebooking.repository.ShowtimeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShowtimeService {

    private static final Logger log = LoggerFactory.getLogger(ShowtimeService.class);

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;

    public ShowtimeService(ShowtimeRepository showtimeRepository, MovieRepository movieRepository,
                          ScreenRepository screenRepository) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
        this.screenRepository = screenRepository;
    }

    public List<ShowtimeDto> getShowtimesForMovie(Long movieId) {
        return showtimeRepository.findUpcomingShowtimes(movieId, LocalDateTime.now()).stream()
                .map(ShowtimeDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ShowtimeDto> getShowtimesForMovieAndDate(Long movieId, LocalDate date) {
        return showtimeRepository.findByMovieIdAndShowDateAndStatus(movieId, date, ShowtimeStatus.ACTIVE).stream()
                .map(ShowtimeDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ShowtimeDto> getShowtimesForTheaterAndDate(Long theaterId, LocalDate date) {
        return showtimeRepository.findByTheaterAndDate(theaterId, date).stream()
                .map(ShowtimeDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ShowtimeDto> getShowtimesByDate(LocalDate date) {
        return showtimeRepository.findByShowDateAndActive(date).stream()
                .map(ShowtimeDto::fromEntity)
                .collect(Collectors.toList());
    }

    public ShowtimeDto getShowtimeById(Long id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime", "id", id));
        return ShowtimeDto.fromEntity(showtime);
    }

    @Transactional
    public ShowtimeDto createShowtime(Long movieId, Long screenId, LocalDate showDate,
                                      LocalTime showTime, BigDecimal basePrice, String pricingStrategy) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", movieId));
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen", "id", screenId));

        LocalDateTime startTime = LocalDateTime.of(showDate, showTime);
        LocalDateTime endTime = startTime.plusMinutes(movie.getDuration() + 30); // 30 min buffer

        // Check conflicts
        List<Showtime> conflicts = showtimeRepository.findConflictingShowtimes(screenId, startTime, endTime);
        if (!conflicts.isEmpty()) {
            throw new BadRequestException("Time slot conflicts with existing showtime on this screen");
        }

        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setScreen(screen);
        showtime.setShowDate(showDate);
        showtime.setShowTime(showTime);
        showtime.setShowDatetime(startTime);
        showtime.setEndDatetime(endTime);
        showtime.setBasePrice(basePrice);
        showtime.setPricingStrategy(pricingStrategy != null ? pricingStrategy : "DYNAMIC");
        showtime.setAvailableSeats(screen.getTotalSeats());
        showtime.setTotalSeats(screen.getTotalSeats());
        showtime.setStatus(ShowtimeStatus.ACTIVE);

        showtime = showtimeRepository.save(showtime);
        log.info("Created showtime {} for movie '{}' on screen '{}' at {}",
                showtime.getId(), movie.getTitle(), screen.getScreenName(), startTime);

        return ShowtimeDto.fromEntity(showtime);
    }

    @Transactional
    public void cancelShowtime(Long id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime", "id", id));
        showtime.setStatus(ShowtimeStatus.CANCELLED);
        showtimeRepository.save(showtime);
        log.info("Cancelled showtime {}", id);
    }

    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.findAll();
    }
}
