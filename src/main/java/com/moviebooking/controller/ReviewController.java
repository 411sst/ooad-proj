package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.dto.CreateReviewRequest;
import com.moviebooking.dto.ReviewDto;
import com.moviebooking.entity.User;
import com.moviebooking.repository.UserRepository;
import com.moviebooking.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository;

    public ReviewController(ReviewService reviewService, UserRepository userRepository) {
        this.reviewService = reviewService;
        this.userRepository = userRepository;
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<List<ReviewDto>>> getMovieReviews(@PathVariable Long movieId) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getReviewsForMovie(movieId)));
    }

    @GetMapping("/movie/{movieId}/rating")
    public ResponseEntity<ApiResponse<Double>> getAverageRating(@PathVariable Long movieId) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getAverageRating(movieId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDto>> createReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateReviewRequest request) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        ReviewDto review = reviewService.createReview(user, request.getMovieId(),
                request.getRating(), request.getReviewText());
        return ResponseEntity.ok(ApiResponse.success(review));
    }

    @PutMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<ReviewDto>> updateReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long movieId,
            @Valid @RequestBody CreateReviewRequest request) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        ReviewDto review = reviewService.updateReview(user, movieId, request.getRating(), request.getReviewText());
        return ResponseEntity.ok(ApiResponse.success(review));
    }

    @DeleteMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<String>> deleteReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long movieId) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        reviewService.deleteReview(user, movieId);
        return ResponseEntity.ok(ApiResponse.success("Review deleted"));
    }
}
