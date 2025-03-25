package com.example.socialcoffee.service;

import com.example.socialcoffee.configuration.ConfigResource;
import com.example.socialcoffee.dto.request.EmbedRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class SentenceTransformerService {
    private final RestTemplate restTemplate;
    private final ConfigResource configResource;
    private final HttpHeaders defaultHeaders;
    public Float[] generateEmbeddingDescription(String description) {
        if (Objects.isNull(description)) return null;
        log.info("Start generating embedding description");
        String embeddingUrl = configResource.getEmbeddingUrl() + configResource.getEmbeddingModel();
        EmbedRequest embedRequest = new EmbedRequest(description);

        // Retry parameters
        int maxRetries = 5;
        int retryCount = 0;
        int retryDelayMs = 1000; // Initial delay of 1 second

        while (retryCount < maxRetries) {
            try {
                ResponseEntity<Float[]> embeddingResponse = restTemplate.exchange(
                        embeddingUrl,
                        HttpMethod.POST,
                        new HttpEntity<>(embedRequest, defaultHeaders), Float[].class);
                Float[] embeddingDescription = embeddingResponse.getBody();
                log.info("Finish generating embedding description");
                return embeddingDescription;
            } catch (RestClientException e) {
                retryCount++;

                // Check if it's a 503 error or other potentially recoverable error
                if (e instanceof HttpStatusCodeException
                        && ((HttpStatusCodeException) e).getStatusCode().value() == 503) {
                    if (retryCount < maxRetries) {
                        log.warn("Service unavailable (503), retry attempt {}/{}", retryCount, maxRetries);
                        try {
                            // Exponential backoff
                            Thread.sleep(retryDelayMs * retryCount);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            log.error("Thread interrupted during retry delay", ie);
                        }
                    } else {
                        log.error("FAILED generateEmbeddingDescription after {} retries", maxRetries, e);
                        throw new RuntimeException("Failed to generate embedding after maximum retries", e);
                    }
                } else {
                    // For other types of exceptions, log and rethrow immediately
                    log.error("FAILED generateEmbeddingDescription", e);
                    throw new RuntimeException(e);
                }
            }
        }

        // This code will only be reached if all retries failed
        log.error("FAILED generateEmbeddingDescription after all retry attempts");
        throw new RuntimeException("Failed to generate embedding after maximum retries");
    }
}
