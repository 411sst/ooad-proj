package com.moviebooking.repository;

import com.moviebooking.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {

    List<Screen> findByTheaterId(Long theaterId);

    List<Screen> findByTheaterIdAndIsActiveTrue(Long theaterId);

    Optional<Screen> findByTheaterIdAndScreenNumber(Long theaterId, Integer screenNumber);
}
