package com.example.socialcoffee.service;

import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.dto.response.GoogleUserResponse;
import com.example.socialcoffee.dto.response.JwtTokenResponse;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
//    private final StringRedis redisTemplate;
    private final MailService mailService;
    @Value("#{'${email.list}'.split(',')}")
    private List<String> ccList;
    @Value("${oauth2.client.registration.google.userinfo-endpoint}")
    private String userInfoEndpoint;
    @Value("${oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    @Value("${redis.prefix}")
    private String redisPrefix;

    public ResponseEntity<ResponseMetaData> getGoogleAuthorizationCodeRequestUrl(String redirectUri) {
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             new GoogleAuthorizationCodeRequestUrl(
                                                                     clientId,
                                                                     redirectUri,
                                                                     Arrays.asList("email",
                                                                                   "profile",
                                                                                   "openid")
                                                             ).build()));
    }

    public ResponseEntity<ResponseMetaData> getJwtToken(String code, String redirectUri) {
        try {
            NetHttpTransport transport = new NetHttpTransport();
            String accessToken = getGoogleAccessToken(code, transport, redirectUri);
            GoogleUserResponse userInfo = getUserInfoFromGoogle(accessToken, transport);
            User user = userRepository.findByEmailAndStatus(userInfo.getEmail(), Status.ACTIVE.ordinal())
                    .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
            if (user.getProfilePhoto() == null) {
                sendWelcomeEmail(user);
            }
            user.setProfilePhoto(userInfo.getPicture());
            user = userRepository.save(user);
            String jwtToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                                 new JwtTokenResponse(jwtToken,
                                                                                      refreshToken)));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

//    @Override
//    public ResponseEntity<ResponseMetaData> refreshToken(final TokenRefreshRequest request) {
//        try {
//            SignedJWT signedJWT = jwtService.verifyToken(request.refresh(), REFRESH_TOKEN_PREFIX);
//            String userEmail = signedJWT.getJWTClaimsSet().getSubject();
//            User user = userRepository.findByEmail(userEmail)
//                    .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
//            String jwtToken = jwtService.generateAccessToken(user);
//            return new JwtTokenResponse(jwtToken, request.refresh());
//        } catch (JOSEException | ParseException e) {
//            throw new InvalidTokenException(e.getMessage());
//        }
//    }
//
//    @Override
//    public void logOut() {
//        redisTemplate.delete(redisPrefix + TOKEN_PREFIX + SecurityUtil.getUserEmail());
//        redisTemplate.delete(redisPrefix + REFRESH_TOKEN_PREFIX + SecurityUtil.getUserEmail());
//    }

    private String getGoogleAccessToken(String code, NetHttpTransport transport, String redirectUri) throws IOException {
        String accessToken;
        GsonFactory gsonFactory = new GsonFactory();
        accessToken = new GoogleAuthorizationCodeTokenRequest(
                transport,
                gsonFactory,
                clientId,
                clientSecret,
                code,
                redirectUri
        ).execute().getAccessToken();
        return accessToken;
    }

    private GoogleUserResponse getUserInfoFromGoogle(String accessToken, NetHttpTransport transport) throws IOException {
        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
        HttpRequest request = transport
                .createRequestFactory(credential)
                .buildGetRequest(new GenericUrl(userInfoEndpoint));
        HttpResponse response = request.execute();
        String userInfoResponse = response.parseAsString();
        return objectMapper.readValue(userInfoResponse, GoogleUserResponse.class);
    }

    private void sendWelcomeEmail(User user) {
        String subject = "WELCOME TO GOCOFFEE";
        Context context = new Context();
        Map<String, Object> variables = new HashMap<>();
        variables.put("fullName", user.getUsername());
        context.setVariables(variables);
        Thread emailThread = new Thread(() -> {
            try {
                mailService.sendEmailWithHtmlTemplate(user.getEmail(), ccList, subject, "welcome-mail-template", context);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        });
        emailThread.start();
    }
}
