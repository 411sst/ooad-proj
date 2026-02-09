package com.moviebooking.dto;

import com.moviebooking.entity.Review;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewDto {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long userId;
    private String userName;
    private Integer rating;
    private String reviewText;
    private LocalDateTime createdAt;

    public static ReviewDto fromEntity(Review r) {
        ReviewDto dto = new ReviewDto();
        dto.setId(r.getId());
        dto.setMovieId(r.getMovie().getId());
        dto.setMovieTitle(r.getMovie().getTitle());
        dto.setUserId(r.getUser().getId());
        dto.setUserName(r.getUser().getFullName());
        dto.setRating(r.getRating());
        dto.setReviewText(r.getReviewText());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }
}
