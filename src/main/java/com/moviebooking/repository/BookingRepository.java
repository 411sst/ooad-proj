package com.moviebooking.repository;

import com.moviebooking.entity.Booking;
import com.moviebooking.entity.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingReference(String bookingReference);

    Page<Booking> findByUserIdOrderByBookingDatetimeDesc(Long userId, Pageable pageable);

    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);

    List<Booking> findByShowtimeId(Long showtimeId);

    List<Booking> findByShowtimeIdAndStatus(Long showtimeId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.showtime.showDate >= :date AND b.status = 'CONFIRMED' ORDER BY b.showtime.showDatetime ASC")
    List<Booking> findUpcomingBookings(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.showtime.id = :showtimeId AND b.status IN ('CONFIRMED', 'LOCKED')")
    long countActiveBookingsForShowtime(@Param("showtimeId") Long showtimeId);

    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.status = 'CONFIRMED' AND b.bookingDatetime BETWEEN :start AND :end")
    java.math.BigDecimal getTotalRevenue(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
