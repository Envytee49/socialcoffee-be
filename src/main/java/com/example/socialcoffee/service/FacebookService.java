package com.example.socialcoffee.service;

import com.example.socialcoffee.configuration.AuthConfig;
import com.example.socialcoffee.model.FacebookUserInfo;
import com.example.socialcoffee.constants.CommonConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacebookService {
    private final AuthConfig authConfig;
    public FacebookUserInfo getUserInfoFromFacebook(String accessToken) {
        try {
            HttpClient client = HttpClients.custom().build();
            HttpUriRequest request = RequestBuilder.get()
                    .setUri(authConfig.getUserFacebookApi().concat(accessToken))
                    .setHeader(HttpHeaders.CONTENT_TYPE, CommonConstant.CONTENT_TYPE)
                    .build();
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.warn("FAILED while get user info from facebook");
                return null;
            }

            String result = EntityUtils.toString(response.getEntity());

            return (new ObjectMapper()).readValue(result, FacebookUserInfo.class);
        } catch (Exception e) {
            log.error("FAILED while get user info from facebook", e);
            return null;
        }
    }
}
