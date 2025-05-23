package com.example.socialcoffee.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class MoodCountDto {
    private Map<String, Long> moodCounts;
    private List<String> userMoodCounts;
}
