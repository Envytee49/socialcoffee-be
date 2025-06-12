package com.example.socialcoffee.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MoodCount {
    private String mood;
    private Long count;
}
