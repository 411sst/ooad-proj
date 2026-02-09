package com.moviebooking.repository;

import com.moviebooking.entity.Showtime;
import com.moviebooking.entity.enums.ShowtimeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    List<Showtime> findByMovieIdAndStatus(Long movieId, ShowtimeStatus status);

    List<Showtime> findByMovieIdAndShowDateAndStatus(Long movieId, LocalDate showDate, ShowtimeStatus status);

    List<Showtime> findByScreenIdAndShowDate(Long screenId, LocalDate showDate);

    @Query("SELECT s FROM Showtime s WHERE s.movie.id = :movieId AND s.showDatetime >= :now AND s.status = 'ACTIVE' ORDER BY s.showDatetime ASC")
    List<Showtime> findUpcomingShowtimes(@Param("movieId") Long movieId, @Param("now") LocalDateTime now);

    @Query("SELECT s FROM Showtime s WHERE s.screen.id = :screenId AND s.showDatetime < :endTime AND s.endDatetime > :startTime AND s.status = 'ACTIVE'")
    List<Showtime> findConflictingShowtimes(@Param("screenId") Long screenId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT s FROM Showtime s WHERE s.showDate = :date AND s.status = 'ACTIVE' ORDER BY s.movie.title, s.showTime")
    List<Showtime> findByShowDateAndActive(@Param("date") LocalDate date);

    @Query("SELECT s FROM Showtime s WHERE s.screen.theater.id = :theaterId AND s.showDate = :date AND s.status = 'ACTIVE'")
    List<Showtime> findByTheaterAndDate(@Param("theaterId") Long theaterId, @Param("date") LocalDate date);
}
