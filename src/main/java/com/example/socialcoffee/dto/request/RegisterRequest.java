package com.example.socialcoffee.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "username must not be blank")
    private String username;
    @NotBlank(message = "password must not be blank")
    private String password;
    @NotBlank(message = "confirm password must not be blank")
    private String confirmPassword;
    @NotBlank(message = "display name must not be blank")
    private String displayName;
    @NotBlank(message = "full name must not be blank")
    private String fullName;
}
