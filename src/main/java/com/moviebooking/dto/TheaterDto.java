package com.moviebooking.dto;

import com.moviebooking.entity.Theater;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TheaterDto {
    private Long id;
    private String name;
    private String location;
    private String city;
    private String state;
    private String pincode;
    private String theaterType;
    private Integer totalScreens;
    private String facilities;
    private Boolean isActive;
    private List<ScreenDto> screens;

    @Getter
    @Setter
    public static class ScreenDto {
        private Long id;
        private Integer screenNumber;
        private String screenName;
        private String screenType;
        private Integer totalSeats;
        private Integer rows;
        private Integer columns;
        private String soundSystem;
        private String screenSize;
        private String specialFeatures;
        private Boolean isActive;
    }

    public static TheaterDto fromEntity(Theater t) {
        TheaterDto dto = new TheaterDto();
        dto.setId(t.getId());
        dto.setName(t.getName());
        dto.setLocation(t.getLocation());
        dto.setCity(t.getCity());
        dto.setState(t.getState());
        dto.setPincode(t.getPincode());
        dto.setTheaterType(t.getTheaterType().name());
        dto.setTotalScreens(t.getTotalScreens());
        dto.setFacilities(t.getFacilities());
        dto.setIsActive(t.getIsActive());
        return dto;
    }
}
