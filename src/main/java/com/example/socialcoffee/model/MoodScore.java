package com.example.socialcoffee.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MoodScore {
    private Long shopId;
    private Double score;
}
