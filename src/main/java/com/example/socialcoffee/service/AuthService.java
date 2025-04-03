package com.example.socialcoffee.service;

import com.example.socialcoffee.domain.AuthProvider;
import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.domain.UserAuthConnection;
import com.example.socialcoffee.dto.response.GoogleUserResponse;
import com.example.socialcoffee.dto.response.JwtTokenResponse;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.AuthAction;
import com.example.socialcoffee.enums.AuthProviderEnum;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.repository.AuthProviderRepository;
import com.example.socialcoffee.repository.UserAuthConnectionRepository;
import com.example.socialcoffee.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserAuthConnectionRepository userAuthConnectionRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final AuthProviderRepository authProviderRepository;
    //    private final StringRedis redisTemplate;
//    private final MailService mailService;
//    @Value("#{'${email.list}'.split(',')}")
//    private List<String> ccList;
    @Value("${oauth2.client.registration.google.userinfo-endpoint}")
    private String userInfoEndpoint;
    @Value("${oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    @Value("${redis.prefix}")
    private String redisPrefix;

    public ResponseEntity<ResponseMetaData> authWithGoogle(String code, String redirectUri, String authAction) {
        try {
            NetHttpTransport transport = new NetHttpTransport();
            String accessToken = getGoogleAccessToken(code, transport, redirectUri);
            GoogleUserResponse userInfo = getUserInfoFromGoogle(accessToken, transport);
            Optional<User> optionalUser = userRepository.findByEmailAndStatus(userInfo.getEmail(), Status.ACTIVE.getValue());
            if(AuthAction.LOGIN.getValue().equalsIgnoreCase(authAction)) {
                if(optionalUser.isEmpty()) {
                    return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_REGISTERED)));
                }
                User user = optionalUser.get();
                user.setProfilePhoto(userInfo.getPicture());
                user = userRepository.save(user);
                String jwtToken = jwtService.generateAccessToken(user);
                String refreshToken = jwtService.generateRefreshToken(user);
                return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                                     new JwtTokenResponse(jwtToken,
                                                                                          refreshToken)));
            }
            else {
                if(optionalUser.isPresent()) {
                    return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.ALREADY_REGISTER)));
                }
                User user = new User(userInfo);
                user = userRepository.save(user);
                AuthProvider authProvider = authProviderRepository.findByName(AuthProviderEnum.GOOGLE.getValue());
                UserAuthConnection userAuthConnection = new UserAuthConnection(user.getId(),
                                                                               authProvider.getId());
                userAuthConnectionRepository.save(userAuthConnection);
                return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
            }
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

//    private void sendWelcomeEmail(User user) {
//        String subject = "WELCOME TO GOCOFFEE";
//        Context context = new Context();
//        Map<String, Object> variables = new HashMap<>();
//        variables.put("fullName", user.getUsername());
//        context.setVariables(variables);
//        Thread emailThread = new Thread(() -> {
//            try {
//                mailService.sendEmailWithHtmlTemplate(user.getEmail(), ccList, subject, "welcome-mail-template", context);
//            } catch (MessagingException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        emailThread.start();
//    }
}
