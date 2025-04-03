package com.example.socialcoffee.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleLoginRequest {
    private String code;
    @JsonProperty("redirect_url")
    private String redirectUrl;
}
