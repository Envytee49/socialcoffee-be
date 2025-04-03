package com.example.socialcoffee.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PageDtoIn {
    @Min(value = 1)
    private Integer page = 1;

    @Min(value = 1)
    @Max(value = 500)
    private Integer size = 10;
}
