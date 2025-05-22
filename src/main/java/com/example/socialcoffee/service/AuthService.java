package com.example.socialcoffee.service;

import com.example.socialcoffee.configuration.AuthConfig;
import com.example.socialcoffee.domain.AuthProvider;
import com.example.socialcoffee.domain.Role;
import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.domain.UserAuthConnection;
import com.example.socialcoffee.dto.request.BasicAuthRequest;
import com.example.socialcoffee.dto.response.LoginResponse;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.*;
import com.example.socialcoffee.model.FacebookUserInfo;
import com.example.socialcoffee.model.GoogleUserInfo;
import com.example.socialcoffee.neo4j.NUser;
import com.example.socialcoffee.repository.postgres.UserAuthConnectionRepository;
import com.example.socialcoffee.repository.postgres.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserAuthConnectionRepository userAuthConnectionRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final CacheableService cacheableService;
    private final PasswordEncoder passwordEncoder;
    private final ValidationService validationService;
    //    private final StringRedis redisTemplate;
//    private final MailService mailService;
//    @Value("#{'${email.list}'.split(',')}")
//    private List<String> ccList;
    private final AuthConfig authConfig;
    private final GoogleService googleService;
    private final FacebookService facebookService;
    private final RepoService repoService;
    private final NotificationService notificationService;

    public ResponseEntity<ResponseMetaData> authWithGoogle(String code,
                                                           String redirectUri,
                                                           String authAction) {
        try {
            GoogleUserInfo userInfo = googleService.getUserInfoFromGoogle(code,
                                                                          redirectUri);
            if (Objects.isNull(userInfo))
                return ResponseEntity.internalServerError().body(new ResponseMetaData(new MetaDTO(MetaData.GOOGLE_ERROR)));

            Optional<User> optionalUser = userRepository.findByEmailAndStatus(userInfo.getEmail(),
                                                                              Status.ACTIVE.getValue());
            if (AuthAction.LOGIN.getValue().equalsIgnoreCase(authAction)) {
                if (optionalUser.isEmpty()) {
                    return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_REGISTERED)));
                }
                User user = optionalUser.get();
                boolean isLoginFirstTime = Objects.isNull(user.getLastLogin());
                user.setLastLogin(LocalDateTime.now());
                user = userRepository.save(user);
                String jwtToken = jwtService.generateAccessToken(user);
                String refreshToken = jwtService.generateRefreshToken(user);
                return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                                     new LoginResponse(user.getRoles().getFirst().getName(),
                                                                                       jwtToken,
                                                                                       refreshToken,
                                                                                       isLoginFirstTime)));
            } else {
                if (optionalUser.isPresent()) {
                    return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.ALREADY_REGISTER)));
                }
                Role role = cacheableService.findRole(RoleEnum.USER.getValue());
                User user = new User(userInfo);
                user.setRoles(List.of(role));
                final User saved = userRepository.save(user);
                NUser nUser = NUser.builder()
                        .id(saved.getId())
                        .displayName(user.getDisplayName())
                        .profilePhoto(user.getProfilePhoto())
                        .build();
                repoService.saveNUser(nUser);
                AuthProvider authProvider = cacheableService.findProvider(AuthProviderEnum.GOOGLE.getValue());
                UserAuthConnection userAuthConnection = new UserAuthConnection(user.getId(),
                                                                               authProvider.getId());
                userAuthConnectionRepository.save(userAuthConnection);
                CompletableFuture.runAsync(() -> notificationService.pushNotiToUsersWhenSuccessRegister(saved));
                return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public ResponseEntity<ResponseMetaData> authWithFacebook(String accessToken,
                                                             String authAction) {
        FacebookUserInfo userInfo = facebookService.getUserInfoFromFacebook(accessToken);
        if (Objects.isNull(userInfo))
            return ResponseEntity.internalServerError().body(new ResponseMetaData(new MetaDTO(MetaData.FACEBOOK_ERROR)));
        Optional<User> optionalUser = userRepository.findByEmailAndStatus(userInfo.getEmail(),
                                                                          Status.ACTIVE.getValue());
        if (AuthAction.LOGIN.getValue().equalsIgnoreCase(authAction)) {
            if (optionalUser.isEmpty()) {
                return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_REGISTERED)));
            }
            User user = optionalUser.get();
            user = userRepository.save(user);
            String jwtToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                                 new LoginResponse(user.getRoles().getFirst().getName(),
                                                                                   jwtToken,
                                                                                   refreshToken)));
        } else {
            if (optionalUser.isPresent()) {
                return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.ALREADY_REGISTER)));
            }
            Role role = cacheableService.findRole(RoleEnum.USER.getValue());
            User user = new User(userInfo);
            user.setRoles(List.of(role));
            user = userRepository.save(user);
            AuthProvider authProvider = cacheableService.findProvider(AuthProviderEnum.FACEBOOK.getValue());
            UserAuthConnection userAuthConnection = new UserAuthConnection(user.getId(),
                                                                           authProvider.getId());
            userAuthConnectionRepository.save(userAuthConnection);
            return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
        }
    }

    public ResponseEntity<ResponseMetaData> basicAuth(BasicAuthRequest request,
                                                      String authAction) {
        String username = request.getUsername();
        String password = request.getPassword();
        String displayName = request.getDisplayName();
        String fullName = request.getFullName();
        final List<MetaDTO> metaDTOS = validationService.validateBasicAuthRequest(request,
                                                                                  authAction);
        if (!CollectionUtils.isEmpty(metaDTOS)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(metaDTOS));
        }

        Optional<User> optionalUser = userRepository.findByUsernameAndStatus(username,
                                                                             Status.ACTIVE.getValue());

        if (AuthAction.LOGIN.getValue().equalsIgnoreCase(authAction)) {
            // Login logic
            if (optionalUser.isEmpty()) {
                return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_REGISTERED)));
            }

            User user = optionalUser.get();
            // Verify password
            if (!passwordEncoder.matches(password,
                                         user.getPassword())) {
                return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.INVALID_CREDENTIALS)));
            }
            boolean isLoginFirstTime = Objects.isNull(user.getLastLogin());
            String jwtToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                                 new LoginResponse(user.getRoles().getFirst().getName(),
                                                                                   jwtToken,
                                                                                   refreshToken,
                                                                                   isLoginFirstTime)));
        } else {
            // Register logic
            if (optionalUser.isPresent()) {
                return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.ALREADY_REGISTER)));
            }

            Role role = cacheableService.findRole(RoleEnum.USER.getValue());
            User user = new User();
            user.setUsername(username);
            user.setDisplayName(displayName);
            user.setName(fullName);
            user.setPassword(passwordEncoder.encode(password));
            user.setStatus(Status.ACTIVE.getValue());
            user.setRoles(List.of(role));

            final User saved = userRepository.save(user);
            NUser nUser = NUser.builder()
                    .id(saved.getId())
                    .displayName(user.getDisplayName())
                    .profilePhoto(user.getProfilePhoto())
                    .build();
            repoService.saveNUser(nUser);
            CompletableFuture.runAsync(() -> notificationService.pushNotiToUsersWhenSuccessRegister(saved));
            return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
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
