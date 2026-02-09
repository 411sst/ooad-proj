package com.moviebooking.repository;

import com.moviebooking.entity.SeatLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatLockRepository extends JpaRepository<SeatLock, Long> {

    Optional<SeatLock> findBySeatIdAndShowtimeIdAndIsActiveTrue(Long seatId, Long showtimeId);

    List<SeatLock> findByShowtimeIdAndIsActiveTrue(Long showtimeId);

    List<SeatLock> findByUserIdAndShowtimeIdAndIsActiveTrue(Long userId, Long showtimeId);

    @Query("SELECT sl FROM SeatLock sl WHERE sl.isActive = true AND sl.lockedUntil < :now")
    List<SeatLock> findExpiredLocks(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE SeatLock sl SET sl.isActive = false WHERE sl.isActive = true AND sl.lockedUntil < :now")
    int releaseExpiredLocks(@Param("now") LocalDateTime now);

    @Query("SELECT sl.seat.id FROM SeatLock sl WHERE sl.showtime.id = :showtimeId AND sl.isActive = true AND sl.lockedUntil > :now")
    List<Long> findLockedSeatIdsForShowtime(@Param("showtimeId") Long showtimeId, @Param("now") LocalDateTime now);
}
