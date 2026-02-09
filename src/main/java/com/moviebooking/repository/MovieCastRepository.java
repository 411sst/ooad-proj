package com.moviebooking.repository;

import com.moviebooking.entity.MovieCast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieCastRepository extends JpaRepository<MovieCast, Long> {

    List<MovieCast> findByMovieIdOrderByDisplayOrderAsc(Long movieId);

    List<MovieCast> findByPersonNameContainingIgnoreCase(String personName);
}
