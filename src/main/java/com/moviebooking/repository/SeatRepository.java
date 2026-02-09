package com.moviebooking.repository;

import com.moviebooking.entity.Seat;
import com.moviebooking.entity.enums.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByScreenId(Long screenId);

    List<Seat> findByScreenIdAndIsAvailableTrue(Long screenId);

    List<Seat> findByScreenIdAndSeatType(Long screenId, SeatType seatType);

    List<Seat> findByIdIn(List<Long> seatIds);
}
