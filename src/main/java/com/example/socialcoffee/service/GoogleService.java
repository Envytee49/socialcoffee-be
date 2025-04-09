package com.example.socialcoffee.service;

import com.example.socialcoffee.configuration.AuthConfig;
import com.example.socialcoffee.model.GoogleUserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleService {
    private final NetHttpTransport transport = new NetHttpTransport();
    private final AuthConfig authConfig;
    private final ObjectMapper objectMapper;
    public GoogleUserInfo getUserInfoFromGoogle(String code, String redirectUri) throws IOException {
        String accessToken;
        GsonFactory gsonFactory = new GsonFactory();
        accessToken = new GoogleAuthorizationCodeTokenRequest(
                transport,
                gsonFactory,
                authConfig.getClientId(),
                authConfig.getClientSecret(),
                code,
                redirectUri
        ).execute().getAccessToken();
        return getUserInfoFromGoogle(accessToken);
    }

    @SneakyThrows
    public GoogleUserInfo getUserInfoFromGoogle(String accessToken) {
        String userInfoResponse;
        try {
            Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
            HttpRequest request = transport
                    .createRequestFactory(credential)
                    .buildGetRequest(new GenericUrl(authConfig.getUserInfoEndpoint()));
            HttpResponse response = request.execute();
            userInfoResponse = response.parseAsString();
        } catch (Exception e) {
            log.error("FAILED getUserInfoFromGoogle: ", e);
            return null;
        }
        return objectMapper.readValue(userInfoResponse, GoogleUserInfo.class);
    }
}
