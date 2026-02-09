package com.moviebooking.service;

import com.moviebooking.entity.enums.BookingStatus;
import com.moviebooking.entity.enums.PaymentStatus;
import com.moviebooking.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final UserRepository userRepository;

    public AnalyticsService(BookingRepository bookingRepository, PaymentRepository paymentRepository,
                           ShowtimeRepository showtimeRepository, MovieRepository movieRepository,
                           TheaterRepository theaterRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
        this.theaterRepository = theaterRepository;
        this.userRepository = userRepository;
    }

    /**
     * Revenue report - daily revenue for the last N days.
     */
    public List<Map<String, Object>> getDailyRevenue(int days) {
        List<Map<String, Object>> report = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);

            BigDecimal revenue = bookingRepository.getTotalRevenue(start, end);
            Map<String, Object> entry = new HashMap<>();
            entry.put("date", date.toString());
            entry.put("revenue", revenue != null ? revenue : BigDecimal.ZERO);
            report.add(entry);
        }
        return report;
    }

    /**
     * Booking status distribution.
     */
    public Map<String, Long> getBookingStatusDistribution() {
        Map<String, Long> distribution = new LinkedHashMap<>();
        for (BookingStatus status : BookingStatus.values()) {
            long count = bookingRepository.findAll().stream()
                    .filter(b -> b.getStatus() == status).count();
            distribution.put(status.name(), count);
        }
        return distribution;
    }

    /**
     * Movie performance - bookings per movie.
     */
    public List<Map<String, Object>> getMoviePerformance() {
        return movieRepository.findAll().stream().map(movie -> {
            Map<String, Object> perf = new HashMap<>();
            perf.put("movieId", movie.getId());
            perf.put("movieTitle", movie.getTitle());
            perf.put("genre", movie.getGenre());
            perf.put("status", movie.getStatus().name());

            long totalBookings = bookingRepository.findAll().stream()
                    .filter(b -> b.getMovie().getId().equals(movie.getId())
                            && b.getStatus() == BookingStatus.CONFIRMED)
                    .count();
            perf.put("totalBookings", totalBookings);

            BigDecimal totalRevenue = bookingRepository.findAll().stream()
                    .filter(b -> b.getMovie().getId().equals(movie.getId())
                            && b.getStatus() == BookingStatus.CONFIRMED)
                    .map(b -> b.getTotalAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            perf.put("totalRevenue", totalRevenue);

            long totalSeatsBooked = bookingRepository.findAll().stream()
                    .filter(b -> b.getMovie().getId().equals(movie.getId())
                            && b.getStatus() == BookingStatus.CONFIRMED)
                    .mapToLong(b -> b.getNumSeats())
                    .sum();
            perf.put("totalSeatsBooked", totalSeatsBooked);

            return perf;
        }).sorted((a, b) -> Long.compare((long) b.get("totalBookings"), (long) a.get("totalBookings")))
                .collect(Collectors.toList());
    }

    /**
     * Theater occupancy rates.
     */
    public List<Map<String, Object>> getTheaterOccupancy() {
        return theaterRepository.findAll().stream().map(theater -> {
            Map<String, Object> occ = new HashMap<>();
            occ.put("theaterId", theater.getId());
            occ.put("theaterName", theater.getName());
            occ.put("theaterType", theater.getTheaterType().name());

            var showtimes = showtimeRepository.findAll().stream()
                    .filter(s -> s.getScreen().getTheater().getId().equals(theater.getId()))
                    .collect(Collectors.toList());

            long totalCapacity = showtimes.stream().mapToLong(s -> s.getTotalSeats()).sum();
            long totalBooked = showtimes.stream().mapToLong(s -> s.getTotalSeats() - s.getAvailableSeats()).sum();

            occ.put("totalShowtimes", showtimes.size());
            occ.put("totalCapacity", totalCapacity);
            occ.put("totalBooked", totalBooked);
            occ.put("occupancyRate", totalCapacity > 0 ?
                    BigDecimal.valueOf(totalBooked).divide(BigDecimal.valueOf(totalCapacity), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP) : BigDecimal.ZERO);

            return occ;
        }).collect(Collectors.toList());
    }

    /**
     * Payment method distribution.
     */
    public Map<String, Long> getPaymentMethodDistribution() {
        Map<String, Long> distribution = new LinkedHashMap<>();
        paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .forEach(p -> {
                    String method = p.getPaymentMethod().name();
                    distribution.merge(method, 1L, Long::sum);
                });
        return distribution;
    }

    /**
     * Summary stats for analytics page.
     */
    public Map<String, Object> getAnalyticsSummary() {
        Map<String, Object> summary = new HashMap<>();

        // Total revenue all time
        BigDecimal totalRevenue = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .map(b -> b.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.put("totalRevenue", totalRevenue);

        // This week revenue
        LocalDateTime weekStart = LocalDate.now().minusDays(7).atStartOfDay();
        BigDecimal weekRevenue = bookingRepository.getTotalRevenue(weekStart, LocalDateTime.now());
        summary.put("weekRevenue", weekRevenue != null ? weekRevenue : BigDecimal.ZERO);

        // Total confirmed bookings
        long confirmedBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED).count();
        summary.put("confirmedBookings", confirmedBookings);

        // Total cancelled bookings
        long cancelledBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.CANCELLED).count();
        summary.put("cancelledBookings", cancelledBookings);

        // Average booking value
        if (confirmedBookings > 0) {
            summary.put("avgBookingValue", totalRevenue.divide(BigDecimal.valueOf(confirmedBookings), 2, RoundingMode.HALF_UP));
        } else {
            summary.put("avgBookingValue", BigDecimal.ZERO);
        }

        // Total users
        summary.put("totalUsers", userRepository.count());

        return summary;
    }
}
