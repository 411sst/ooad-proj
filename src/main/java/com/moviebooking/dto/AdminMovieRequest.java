package com.moviebooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class AdminMovieRequest {

    @NotBlank
    private String title;

    private String description;
    private String synopsis;

    @NotBlank
    private String genre;

    @NotBlank
    private String language;

    @NotNull
    private Integer duration;

    @NotBlank
    private String certification;

    @NotNull
    private LocalDate releaseDate;

    private String posterUrl;
    private String trailerUrl;
    private BigDecimal imdbRating;
    private String status;
    private String director;
    private String producer;
}
