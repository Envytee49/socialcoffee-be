package com.example.socialcoffee.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ConfigResource {
    @Value("${ollama.api.key}")
    private String apiKey;
    @Value("${ollama.embedding.url}")
    private String embeddingUrl;
    @Value("${ollama.generating.url}")
    private String generatingUrl;
    @Value("${ollama.generating.model}")
    private String generatingModel;
    @Value("${ollama.embedding.model}")
    private String embeddingModel;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(final String apiKey) {
        this.apiKey = apiKey;
    }

    public String getEmbeddingUrl() {
        return embeddingUrl;
    }

    public void setEmbeddingUrl(final String embeddingUrl) {
        this.embeddingUrl = embeddingUrl;
    }

    public String getGeneratingUrl() {
        return generatingUrl;
    }

    public void setGeneratingUrl(final String generatingUrl) {
        this.generatingUrl = generatingUrl;
    }

    public String getGeneratingModel() {
        return generatingModel;
    }

    public void setGeneratingModel(final String generatingModel) {
        this.generatingModel = generatingModel;
    }

    public String getEmbeddingModel() {
        return embeddingModel;
    }

    public void setEmbeddingModel(final String embeddingModel) {
        this.embeddingModel = embeddingModel;
    }
}
