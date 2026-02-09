package com.moviebooking.service;

import com.moviebooking.dto.MovieDto;
import com.moviebooking.entity.Movie;
import com.moviebooking.entity.MovieCast;
import com.moviebooking.entity.enums.MovieStatus;
import com.moviebooking.exception.ResourceNotFoundException;
import com.moviebooking.repository.MovieCastRepository;
import com.moviebooking.repository.MovieRepository;
import com.moviebooking.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieCastRepository movieCastRepository;
    private final ReviewRepository reviewRepository;

    public MovieService(MovieRepository movieRepository, MovieCastRepository movieCastRepository,
                       ReviewRepository reviewRepository) {
        this.movieRepository = movieRepository;
        this.movieCastRepository = movieCastRepository;
        this.reviewRepository = reviewRepository;
    }

    public List<MovieDto> getNowShowingMovies() {
        return movieRepository.findByStatus(MovieStatus.NOW_SHOWING).stream()
                .map(this::toMovieDtoWithRating)
                .collect(Collectors.toList());
    }

    public List<MovieDto> getUpcomingMovies() {
        return movieRepository.findByStatus(MovieStatus.UPCOMING).stream()
                .map(this::toMovieDtoWithRating)
                .collect(Collectors.toList());
    }

    public List<MovieDto> getTrendingMovies(int limit) {
        return movieRepository.findTrendingMovies(PageRequest.of(0, limit)).stream()
                .map(this::toMovieDtoWithRating)
                .collect(Collectors.toList());
    }

    public MovieDto getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", id));
        List<MovieCast> castList = movieCastRepository.findByMovieIdOrderByDisplayOrderAsc(id);
        MovieDto dto = MovieDto.fromEntityWithCast(movie, castList);
        enrichWithRating(dto, id);
        return dto;
    }

    public List<MovieDto> searchMovies(String query) {
        return movieRepository.searchMovies(query).stream()
                .map(this::toMovieDtoWithRating)
                .collect(Collectors.toList());
    }

    public List<MovieDto> getMoviesByGenre(String genre) {
        return movieRepository.findByGenreContainingIgnoreCase(genre).stream()
                .map(this::toMovieDtoWithRating)
                .collect(Collectors.toList());
    }

    public List<MovieDto> getMoviesByLanguage(String language) {
        return movieRepository.findByLanguageIgnoreCase(language).stream()
                .map(this::toMovieDtoWithRating)
                .collect(Collectors.toList());
    }

    public Page<MovieDto> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable).map(MovieDto::fromEntity);
    }

    public List<Movie> getAllMoviesRaw() {
        return movieRepository.findAll();
    }

    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    private MovieDto toMovieDtoWithRating(Movie movie) {
        MovieDto dto = MovieDto.fromEntity(movie);
        enrichWithRating(dto, movie.getId());
        return dto;
    }

    private void enrichWithRating(MovieDto dto, Long movieId) {
        Double avgRating = reviewRepository.getAverageRatingForMovie(movieId);
        dto.setAvgUserRating(avgRating);
        dto.setReviewCount(reviewRepository.findByMovieIdOrderByCreatedAtDesc(movieId).size());
    }
}
