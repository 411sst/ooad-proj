package com.moviebooking.controller;

import com.moviebooking.dto.*;
import com.moviebooking.entity.Movie;
import com.moviebooking.entity.enums.TheaterType;
import com.moviebooking.service.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final MovieService movieService;
    private final TheaterService theaterService;
    private final ShowtimeService showtimeService;

    public AdminController(AdminService adminService, MovieService movieService,
                          TheaterService theaterService, ShowtimeService showtimeService) {
        this.adminService = adminService;
        this.movieService = movieService;
        this.theaterService = theaterService;
        this.showtimeService = showtimeService;
    }

    // ---- Dashboard ----
    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getDashboardStats()));
    }

    // ---- Movie Management ----
    @GetMapping("/movies")
    public ResponseEntity<ApiResponse<List<MovieDto>>> getAllMovies() {
        List<MovieDto> movies = movieService.getAllMoviesRaw().stream()
                .map(MovieDto::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(movies));
    }

    @PostMapping("/movies")
    public ResponseEntity<ApiResponse<MovieDto>> createMovie(@Valid @RequestBody AdminMovieRequest req) {
        Movie movie = adminService.createMovie(req.getTitle(), req.getDescription(), req.getSynopsis(),
                req.getGenre(), req.getLanguage(), req.getDuration(), req.getCertification(),
                req.getReleaseDate(), req.getPosterUrl(), req.getTrailerUrl(), req.getImdbRating(),
                req.getStatus(), req.getDirector(), req.getProducer());
        return ResponseEntity.ok(ApiResponse.success(MovieDto.fromEntity(movie)));
    }

    @PutMapping("/movies/{id}")
    public ResponseEntity<ApiResponse<MovieDto>> updateMovie(@PathVariable Long id,
                                                              @RequestBody AdminMovieRequest req) {
        Movie movie = adminService.updateMovie(id, req.getTitle(), req.getDescription(), req.getSynopsis(),
                req.getGenre(), req.getLanguage(), req.getDuration(), req.getCertification(),
                req.getReleaseDate(), req.getPosterUrl(), req.getTrailerUrl(), req.getImdbRating(),
                req.getStatus(), req.getDirector(), req.getProducer());
        return ResponseEntity.ok(ApiResponse.success(MovieDto.fromEntity(movie)));
    }

    @DeleteMapping("/movies/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMovie(@PathVariable Long id) {
        adminService.deleteMovie(id);
        return ResponseEntity.ok(ApiResponse.success("Movie deleted"));
    }

    // ---- Theater Management ----
    @GetMapping("/theaters")
    public ResponseEntity<ApiResponse<List<TheaterDto>>> getAllTheaters() {
        return ResponseEntity.ok(ApiResponse.success(theaterService.getAllTheaters()));
    }

    @GetMapping("/theaters/{id}")
    public ResponseEntity<ApiResponse<TheaterDto>> getTheater(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(theaterService.getTheaterById(id)));
    }

    @PostMapping("/theaters")
    public ResponseEntity<ApiResponse<TheaterDto>> createTheater(@Valid @RequestBody AdminTheaterRequest req) {
        TheaterDto theater = theaterService.createTheater(req.getName(), req.getLocation(), req.getCity(),
                req.getState(), req.getPincode(), TheaterType.valueOf(req.getTheaterType()),
                req.getTotalScreens(), req.getFacilities());
        return ResponseEntity.ok(ApiResponse.success(theater));
    }

    // ---- Showtime Management ----
    @PostMapping("/showtimes")
    public ResponseEntity<ApiResponse<ShowtimeDto>> createShowtime(@Valid @RequestBody AdminShowtimeRequest req) {
        ShowtimeDto showtime = showtimeService.createShowtime(req.getMovieId(), req.getScreenId(),
                req.getShowDate(), req.getShowTime(), req.getBasePrice(), req.getPricingStrategy());
        return ResponseEntity.ok(ApiResponse.success(showtime));
    }

    @DeleteMapping("/showtimes/{id}")
    public ResponseEntity<ApiResponse<String>> cancelShowtime(@PathVariable Long id) {
        showtimeService.cancelShowtime(id);
        return ResponseEntity.ok(ApiResponse.success("Showtime cancelled"));
    }

    // ---- User Management ----
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllUsers() {
        List<Map<String, Object>> users = adminService.getAllUsers().stream().map(u -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", u.getId());
            map.put("email", u.getEmail());
            map.put("fullName", u.getFullName());
            map.put("phone", u.getPhone());
            map.put("role", u.getRole().name());
            map.put("isActive", u.getIsActive());
            map.put("createdAt", u.getCreatedAt());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PostMapping("/users/{id}/toggle-status")
    public ResponseEntity<ApiResponse<String>> toggleUserStatus(@PathVariable Long id) {
        adminService.toggleUserStatus(id);
        return ResponseEntity.ok(ApiResponse.success("User status toggled"));
    }

    // ---- Promo Codes ----
    @GetMapping("/promo-codes")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPromoCodes() {
        List<Map<String, Object>> codes = adminService.getAllPromoCodes().stream().map(pc -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", pc.getId());
            map.put("code", pc.getCode());
            map.put("discountType", pc.getDiscountType().name());
            map.put("discountValue", pc.getDiscountValue());
            map.put("minimumAmount", pc.getMinimumAmount());
            map.put("maxDiscount", pc.getMaxDiscount());
            map.put("maxUsage", pc.getMaxUsage());
            map.put("currentUsage", pc.getCurrentUsage());
            map.put("validFrom", pc.getValidFrom());
            map.put("validUntil", pc.getValidUntil());
            map.put("isActive", pc.getIsActive());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(codes));
    }
}
