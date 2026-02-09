package com.moviebooking.dto;

import com.moviebooking.entity.Showtime;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class ShowtimeDto {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long screenId;
    private String screenName;
    private String screenType;
    private Long theaterId;
    private String theaterName;
    private String theaterCity;
    private String theaterType;
    private LocalDate showDate;
    private LocalTime showTime;
    private LocalDateTime showDatetime;
    private BigDecimal basePrice;
    private int availableSeats;
    private int totalSeats;
    private String status;
    private String pricingStrategy;

    public static ShowtimeDto fromEntity(Showtime s) {
        ShowtimeDto dto = new ShowtimeDto();
        dto.setId(s.getId());
        dto.setMovieId(s.getMovie().getId());
        dto.setMovieTitle(s.getMovie().getTitle());
        dto.setScreenId(s.getScreen().getId());
        dto.setScreenName(s.getScreen().getScreenName());
        dto.setScreenType(s.getScreen().getScreenType().name());
        dto.setTheaterId(s.getScreen().getTheater().getId());
        dto.setTheaterName(s.getScreen().getTheater().getName());
        dto.setTheaterCity(s.getScreen().getTheater().getCity());
        dto.setTheaterType(s.getScreen().getTheater().getTheaterType().name());
        dto.setShowDate(s.getShowDate());
        dto.setShowTime(s.getShowTime());
        dto.setShowDatetime(s.getShowDatetime());
        dto.setBasePrice(s.getBasePrice());
        dto.setAvailableSeats(s.getAvailableSeats());
        dto.setTotalSeats(s.getTotalSeats());
        dto.setStatus(s.getStatus().name());
        dto.setPricingStrategy(s.getPricingStrategy());
        return dto;
    }
}
