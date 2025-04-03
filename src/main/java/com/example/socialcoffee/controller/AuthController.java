package com.example.socialcoffee.controller;

import com.example.socialcoffee.dto.request.GoogleLoginRequest;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.AuthAction;
import com.example.socialcoffee.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

//    @GetMapping("/url")
//    public ResponseEntity<ResponseMetaData> auth(@RequestParam("redirect_uri") String redirectUri) {
//        return authService.getGoogleAuthorizationCodeRequestUrl(redirectUri);
//    }

    @PostMapping("/google/login")
    public ResponseEntity<ResponseMetaData> loginWithGoogle(@RequestBody GoogleLoginRequest request) {
        return authService.authWithGoogle(request.getCode(),
                                          request.getRedirectUrl(),
                                          AuthAction.LOGIN.getValue());
    }

    @PostMapping("/google/register")
    public ResponseEntity<ResponseMetaData> registerWithGoogle(@RequestBody GoogleLoginRequest request) {
        return authService.authWithGoogle(request.getCode(),
                                          request.getRedirectUrl(),
                                          AuthAction.REGISTER.getValue());
    }

//    @PostMapping("/refresh")
//    public ResponseEntity<ResponseMetaData> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
//        return authService.refreshToken(request);
//    }
//
//    @PreAuthorize("hasRole('USER')")
//    @PutMapping("/logout")
//    public void logOut() {
//        authService.logOut();
//    }
}
