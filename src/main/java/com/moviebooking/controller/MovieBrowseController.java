package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.dto.MovieDto;
import com.moviebooking.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Public movie browsing API - no authentication required.
 */
@RestController
@RequestMapping("/api/movies")
public class MovieBrowseController {

    private final MovieService movieService;

    public MovieBrowseController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/browse/now-showing")
    public ResponseEntity<ApiResponse<List<MovieDto>>> getNowShowing() {
        return ResponseEntity.ok(ApiResponse.success(movieService.getNowShowingMovies()));
    }

    @GetMapping("/browse/upcoming")
    public ResponseEntity<ApiResponse<List<MovieDto>>> getUpcoming() {
        return ResponseEntity.ok(ApiResponse.success(movieService.getUpcomingMovies()));
    }

    @GetMapping("/browse/trending")
    public ResponseEntity<ApiResponse<List<MovieDto>>> getTrending(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(ApiResponse.success(movieService.getTrendingMovies(limit)));
    }

    @GetMapping("/browse/{id}")
    public ResponseEntity<ApiResponse<MovieDto>> getMovie(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(movieService.getMovieById(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MovieDto>>> searchMovies(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.success(movieService.searchMovies(q)));
    }

    @GetMapping("/browse/genre/{genre}")
    public ResponseEntity<ApiResponse<List<MovieDto>>> getByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(ApiResponse.success(movieService.getMoviesByGenre(genre)));
    }

    @GetMapping("/browse/language/{language}")
    public ResponseEntity<ApiResponse<List<MovieDto>>> getByLanguage(@PathVariable String language) {
        return ResponseEntity.ok(ApiResponse.success(movieService.getMoviesByLanguage(language)));
    }
}
