package com.example.socialcoffee.dto.request;

import com.example.socialcoffee.enums.Privacy;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CollectionRequest {
    @NotBlank
    private String name;
    private String description;
    private Privacy privacy;
}
