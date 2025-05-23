package com.example.socialcoffee.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "username must not be blank")
    private String username;
    @NotBlank(message = "username must not be password")
    private String password;
}
