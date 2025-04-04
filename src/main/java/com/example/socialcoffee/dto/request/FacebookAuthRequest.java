package com.example.socialcoffee.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class FacebookAuthRequest {
    @JsonProperty("access_token")
    private String accessToken;
}
