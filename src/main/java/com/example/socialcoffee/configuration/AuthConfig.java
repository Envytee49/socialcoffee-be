package com.example.socialcoffee.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AuthConfig {
    @Value("${oauth2.client.registration.google.userinfo-endpoint}")
    private String userInfoEndpoint;
    @Value("${oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    @Value("${app.security.user-facebook-api}")
    private String userFacebookApi;

}
