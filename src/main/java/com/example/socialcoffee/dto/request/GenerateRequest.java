package com.example.socialcoffee.dto.request;

import com.example.socialcoffee.configuration.OllamaConfig;
import com.example.socialcoffee.dto.common.Message;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class GenerateRequest {
    private String model;
    private List<Message> messages = new ArrayList<>();
    @JsonProperty("max_tokens")
    private int maxTokens;
    private boolean stream;

    public GenerateRequest(final String model,
                           final Message message) {
        this.model = model;
        this.messages.add(message);
        this.maxTokens = 512;
        this.stream = false;
    }

    public static  GenerateRequest getDefaultMessage(OllamaConfig ollamaConfig, String message) {
        return new GenerateRequest(ollamaConfig.getGeneratingModel(),
                                   new Message("user", message));
    }

}
