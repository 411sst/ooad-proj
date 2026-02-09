package com.moviebooking.repository;

import com.moviebooking.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

    List<BookingSeat> findByBookingId(Long bookingId);

    List<BookingSeat> findByShowtimeId(Long showtimeId);

    @Query("SELECT bs.seat.id FROM BookingSeat bs WHERE bs.showtime.id = :showtimeId AND bs.booking.status IN ('CONFIRMED', 'LOCKED')")
    List<Long> findBookedSeatIdsForShowtime(@Param("showtimeId") Long showtimeId);

    boolean existsBySeatIdAndShowtimeId(Long seatId, Long showtimeId);
}
