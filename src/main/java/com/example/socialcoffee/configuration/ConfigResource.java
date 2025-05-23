package com.example.socialcoffee.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ConfigResource {
    @Value("${app.posts.max-length-comment}")
    private Integer maxLengthCommentPost;

    @Value("${postgres.transaction.manager}")
    private String postgresTransactionManager;

    @Value("${neo4j.transaction.manager}")
    private String neo4jTransactionManager;
}
