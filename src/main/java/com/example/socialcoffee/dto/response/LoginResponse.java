package com.example.socialcoffee.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String role;
    private String token;
    private String refreshToken;
    private boolean firstTimeLogin;

    public LoginResponse(final String role,
                         final String token,
                         final String refreshToken) {

        this.role = role;
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public LoginResponse(final String role,
                         final String token,
                         final String refreshToken,
                         final boolean firstTimeLogin) {

        this.role = role;
        this.token = token;
        this.refreshToken = refreshToken;
        this.firstTimeLogin = firstTimeLogin;
    }
}
