package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.dto.ShowtimeDto;
import com.moviebooking.service.ShowtimeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Public showtime browsing API.
 */
@RestController
@RequestMapping("/api/showtimes")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    public ShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<List<ShowtimeDto>>> getShowtimesForMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(ApiResponse.success(showtimeService.getShowtimesForMovie(movieId)));
    }

    @GetMapping("/movie/{movieId}/date/{date}")
    public ResponseEntity<ApiResponse<List<ShowtimeDto>>> getShowtimesForMovieAndDate(
            @PathVariable Long movieId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success(showtimeService.getShowtimesForMovieAndDate(movieId, date)));
    }

    @GetMapping("/theater/{theaterId}/date/{date}")
    public ResponseEntity<ApiResponse<List<ShowtimeDto>>> getShowtimesForTheater(
            @PathVariable Long theaterId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success(showtimeService.getShowtimesForTheaterAndDate(theaterId, date)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowtimeDto>> getShowtimeById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(showtimeService.getShowtimeById(id)));
    }
}
