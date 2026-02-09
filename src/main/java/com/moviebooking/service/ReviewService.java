package com.moviebooking.service;

import com.moviebooking.dto.ReviewDto;
import com.moviebooking.entity.Movie;
import com.moviebooking.entity.Review;
import com.moviebooking.entity.User;
import com.moviebooking.exception.BadRequestException;
import com.moviebooking.exception.ResourceNotFoundException;
import com.moviebooking.repository.MovieRepository;
import com.moviebooking.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;

    public ReviewService(ReviewRepository reviewRepository, MovieRepository movieRepository) {
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
    }

    public List<ReviewDto> getReviewsForMovie(Long movieId) {
        return reviewRepository.findByMovieIdOrderByCreatedAtDesc(movieId).stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
    }

    public Double getAverageRating(Long movieId) {
        return reviewRepository.getAverageRatingForMovie(movieId);
    }

    @Transactional
    public ReviewDto createReview(User user, Long movieId, Integer rating, String reviewText) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", movieId));

        if (reviewRepository.existsByMovieIdAndUserId(movieId, user.getId())) {
            throw new BadRequestException("You have already reviewed this movie");
        }

        if (rating < 1 || rating > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        Review review = new Review();
        review.setMovie(movie);
        review.setUser(user);
        review.setRating(rating);
        review.setReviewText(reviewText);
        review = reviewRepository.save(review);

        log.info("User {} reviewed movie '{}' with rating {}", user.getEmail(), movie.getTitle(), rating);
        return ReviewDto.fromEntity(review);
    }

    @Transactional
    public ReviewDto updateReview(User user, Long movieId, Integer rating, String reviewText) {
        Review review = reviewRepository.findByMovieIdAndUserId(movieId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Review", "movieId+userId", movieId));

        review.setRating(rating);
        review.setReviewText(reviewText);
        review = reviewRepository.save(review);

        return ReviewDto.fromEntity(review);
    }

    @Transactional
    public void deleteReview(User user, Long movieId) {
        Review review = reviewRepository.findByMovieIdAndUserId(movieId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Review", "movieId+userId", movieId));
        reviewRepository.delete(review);
    }
}
