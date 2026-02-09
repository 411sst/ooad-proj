package com.moviebooking.service;

import com.moviebooking.entity.*;
import com.moviebooking.entity.enums.MovieStatus;
import com.moviebooking.entity.enums.TheaterType;
import com.moviebooking.exception.ResourceNotFoundException;
import com.moviebooking.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;
    private final ShowtimeRepository showtimeRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PromoCodeRepository promoCodeRepository;

    public AdminService(MovieRepository movieRepository, TheaterRepository theaterRepository,
                       ScreenRepository screenRepository, ShowtimeRepository showtimeRepository,
                       BookingRepository bookingRepository, PaymentRepository paymentRepository,
                       UserRepository userRepository, PromoCodeRepository promoCodeRepository) {
        this.movieRepository = movieRepository;
        this.theaterRepository = theaterRepository;
        this.screenRepository = screenRepository;
        this.showtimeRepository = showtimeRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.promoCodeRepository = promoCodeRepository;
    }

    /**
     * Dashboard statistics.
     */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalMovies", movieRepository.count());
        stats.put("nowShowingMovies", movieRepository.findByStatus(MovieStatus.NOW_SHOWING).size());
        stats.put("upcomingMovies", movieRepository.findByStatus(MovieStatus.UPCOMING).size());
        stats.put("totalTheaters", theaterRepository.count());
        stats.put("totalScreens", screenRepository.count());
        stats.put("totalUsers", userRepository.count());
        stats.put("totalBookings", bookingRepository.count());

        // Revenue this month
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = LocalDateTime.now();
        BigDecimal monthlyRevenue = bookingRepository.getTotalRevenue(monthStart, monthEnd);
        stats.put("monthlyRevenue", monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO);

        // Today's showtimes
        stats.put("todayShowtimes", showtimeRepository.findByShowDateAndActive(LocalDate.now()).size());

        // Active promo codes
        stats.put("activePromoCodes", promoCodeRepository.findActivePromoCodes(LocalDateTime.now()).size());

        return stats;
    }

    // ---- Movie CRUD ----

    @Transactional
    public Movie createMovie(String title, String description, String synopsis, String genre,
                            String language, int duration, String certification, LocalDate releaseDate,
                            String posterUrl, String trailerUrl, BigDecimal imdbRating,
                            String status, String director, String producer) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setSynopsis(synopsis);
        movie.setGenre(genre);
        movie.setLanguage(language);
        movie.setDuration(duration);
        movie.setCertification(certification);
        movie.setReleaseDate(releaseDate);
        movie.setPosterUrl(posterUrl);
        movie.setTrailerUrl(trailerUrl);
        movie.setImdbRating(imdbRating);
        movie.setStatus(status != null ? MovieStatus.valueOf(status) : MovieStatus.UPCOMING);
        movie.setDirector(director);
        movie.setProducer(producer);
        movie = movieRepository.save(movie);
        log.info("Admin created movie: {}", movie.getTitle());
        return movie;
    }

    @Transactional
    public Movie updateMovie(Long id, String title, String description, String synopsis, String genre,
                            String language, Integer duration, String certification, LocalDate releaseDate,
                            String posterUrl, String trailerUrl, BigDecimal imdbRating,
                            String status, String director, String producer) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", id));

        if (title != null) movie.setTitle(title);
        if (description != null) movie.setDescription(description);
        if (synopsis != null) movie.setSynopsis(synopsis);
        if (genre != null) movie.setGenre(genre);
        if (language != null) movie.setLanguage(language);
        if (duration != null) movie.setDuration(duration);
        if (certification != null) movie.setCertification(certification);
        if (releaseDate != null) movie.setReleaseDate(releaseDate);
        if (posterUrl != null) movie.setPosterUrl(posterUrl);
        if (trailerUrl != null) movie.setTrailerUrl(trailerUrl);
        if (imdbRating != null) movie.setImdbRating(imdbRating);
        if (status != null) movie.setStatus(MovieStatus.valueOf(status));
        if (director != null) movie.setDirector(director);
        if (producer != null) movie.setProducer(producer);

        movie = movieRepository.save(movie);
        log.info("Admin updated movie: {} (id={})", movie.getTitle(), id);
        return movie;
    }

    @Transactional
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
        log.info("Admin deleted movie id={}", id);
    }

    // ---- Promo Code Management ----

    public List<PromoCode> getAllPromoCodes() {
        return promoCodeRepository.findAll();
    }

    public List<PromoCode> getActivePromoCodes() {
        return promoCodeRepository.findActivePromoCodes(LocalDateTime.now());
    }

    // ---- User Management ----

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setIsActive(!user.getIsActive());
        return userRepository.save(user);
    }
}
