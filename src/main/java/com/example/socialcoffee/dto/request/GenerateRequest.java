package com.example.socialcoffee.dto.request;

import com.example.socialcoffee.configuration.ConfigResource;
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
        this.maxTokens = 500;
        this.stream = false;
    }

    public static  GenerateRequest getDefaultMessage(ConfigResource configResource, String message) {
        return new GenerateRequest(configResource.getGeneratingModel(),
                                   new Message("user", message));
    }

}
