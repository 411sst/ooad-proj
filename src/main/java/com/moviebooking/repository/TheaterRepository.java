package com.moviebooking.repository;

import com.moviebooking.entity.Theater;
import com.moviebooking.entity.enums.TheaterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, Long> {

    List<Theater> findByCity(String city);

    List<Theater> findByTheaterType(TheaterType theaterType);

    List<Theater> findByIsActiveTrue();

    List<Theater> findByCityAndIsActiveTrue(String city);
}
