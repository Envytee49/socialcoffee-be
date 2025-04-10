package com.example.socialcoffee.dto.response;

public record LoginResponse(String role, String token, String refreshToken) {
}
