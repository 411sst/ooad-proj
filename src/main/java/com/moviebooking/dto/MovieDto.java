package com.moviebooking.dto;

import com.moviebooking.entity.Movie;
import com.moviebooking.entity.MovieCast;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class MovieDto {
    private Long id;
    private String title;
    private String description;
    private String synopsis;
    private String genre;
    private String language;
    private Integer duration;
    private String certification;
    private LocalDate releaseDate;
    private String posterUrl;
    private String trailerUrl;
    private BigDecimal imdbRating;
    private String status;
    private String director;
    private String producer;
    private Double avgUserRating;
    private int reviewCount;
    private List<CastDto> cast;

    @Getter
    @Setter
    public static class CastDto {
        private String personName;
        private String roleName;
        private String characterName;
    }

    public static MovieDto fromEntity(Movie m) {
        MovieDto dto = new MovieDto();
        dto.setId(m.getId());
        dto.setTitle(m.getTitle());
        dto.setDescription(m.getDescription());
        dto.setSynopsis(m.getSynopsis());
        dto.setGenre(m.getGenre());
        dto.setLanguage(m.getLanguage());
        dto.setDuration(m.getDuration());
        dto.setCertification(m.getCertification());
        dto.setReleaseDate(m.getReleaseDate());
        dto.setPosterUrl(m.getPosterUrl());
        dto.setTrailerUrl(m.getTrailerUrl());
        dto.setImdbRating(m.getImdbRating());
        dto.setStatus(m.getStatus().name());
        dto.setDirector(m.getDirector());
        dto.setProducer(m.getProducer());
        return dto;
    }

    public static MovieDto fromEntityWithCast(Movie m, List<MovieCast> castList) {
        MovieDto dto = fromEntity(m);
        if (castList != null) {
            dto.setCast(castList.stream().map(mc -> {
                CastDto cd = new CastDto();
                cd.setPersonName(mc.getPersonName());
                cd.setRoleName(mc.getRoleType().name());
                cd.setCharacterName(mc.getCharacterName());
                return cd;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}
