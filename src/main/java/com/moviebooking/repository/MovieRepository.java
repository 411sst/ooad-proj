package com.moviebooking.repository;

import com.moviebooking.entity.Movie;
import com.moviebooking.entity.enums.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByStatus(MovieStatus status);

    Page<Movie> findByStatus(MovieStatus status, Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE m.status = :status ORDER BY m.releaseDate DESC")
    List<Movie> findByStatusOrderByReleaseDateDesc(@Param("status") MovieStatus status);

    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(m.genre) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(m.language) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(m.director) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Movie> searchMovies(@Param("query") String query);

    List<Movie> findByGenreContainingIgnoreCase(String genre);

    List<Movie> findByLanguageIgnoreCase(String language);

    @Query("SELECT m FROM Movie m WHERE m.status = 'NOW_SHOWING' ORDER BY m.imdbRating DESC")
    List<Movie> findTrendingMovies(Pageable pageable);
}
