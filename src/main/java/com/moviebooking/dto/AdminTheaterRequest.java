package com.moviebooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AdminTheaterRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String location;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String pincode;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @NotNull
    private String theaterType; // REGULAR, IMAX, FOUR_DX

    @NotNull
    private Integer totalScreens;

    private String facilities;
}
