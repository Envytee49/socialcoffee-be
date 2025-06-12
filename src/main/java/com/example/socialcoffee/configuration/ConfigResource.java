package com.example.socialcoffee.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
@Getter
public class ConfigResource {
    @Value("${app.posts.max-length-comment}")
    private Integer maxLengthCommentPost;

    @Value("${app.avg-rating-threshold}")
    private Double avgRatingThreshold;

    @Value("${app.mood-count-threshold}")
    private Integer moodCountThreshold;

    @Value("${app.review-recency-threshold-by-week}")
    private Integer reviewRecencyThresholdByWeek;

    @Value("${postgres.transaction.manager}")
    private String postgresTransactionManager;

    @Value("${neo4j.transaction.manager}")
    private String neo4jTransactionManager;

    @Bean(name = "prompt")
    public String prompt() throws IOException {
        Resource resource = new ClassPathResource("prompt.txt");
        return Files.readString(Paths.get(resource.getURI()));
    }
}
