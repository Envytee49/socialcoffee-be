package com.example.socialcoffee.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserSearchRequest {
    @NotBlank(message = "name must not be empty")
    private String name;
}
