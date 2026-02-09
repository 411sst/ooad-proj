package com.moviebooking.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String email;
    private String role;
    private String fullName;

    public LoginResponse(String accessToken, String refreshToken, String email, String role, String fullName) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer";
        this.email = email;
        this.role = role;
        this.fullName = fullName;
    }
}
