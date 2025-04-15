package com.example.socialcoffee.controller;

import com.example.socialcoffee.dto.request.BasicAuthRequest;
import com.example.socialcoffee.dto.request.FacebookAuthRequest;
import com.example.socialcoffee.dto.request.GoogleAuthRequest;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.AuthAction;
import com.example.socialcoffee.service.AuthService;
import com.example.socialcoffee.service.UserService;
import com.example.socialcoffee.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/validate")
    public ResponseEntity<ResponseMetaData> validate() {
        Long userId = SecurityUtil.getUserId();
        return userService.getProfile(userId);
    }

    @PostMapping("/google/login")
    public ResponseEntity<ResponseMetaData> loginWithGoogle(@RequestBody GoogleAuthRequest request) {
        return authService.authWithGoogle(request.getCode(),
                                          request.getRedirectUrl(),
                                          AuthAction.LOGIN.getValue());
    }

    @PostMapping("/google/register")
    public ResponseEntity<ResponseMetaData> registerWithGoogle(@RequestBody GoogleAuthRequest request) {
        return authService.authWithGoogle(request.getCode(),
                                          request.getRedirectUrl(),
                                          AuthAction.REGISTER.getValue());
    }

    @PostMapping("/facebook/login")
    public ResponseEntity<ResponseMetaData> loginWithFacebook(@RequestBody FacebookAuthRequest request) {
        return authService.authWithFacebook(request.getAccessToken(),
                                            AuthAction.LOGIN.getValue());
    }

    @PostMapping("/facebook/register")
    public ResponseEntity<ResponseMetaData> registerWithFacebook(@RequestBody FacebookAuthRequest request) {
        return authService.authWithFacebook(request.getAccessToken(),
                                            AuthAction.REGISTER.getValue());
    }

    @PostMapping("/basic/login")
    public ResponseEntity<ResponseMetaData> basicLogin(@RequestBody BasicAuthRequest request) {
        return authService.basicAuth(request,
                                     AuthAction.LOGIN.getValue());
    }

    @PostMapping("/basic/register")
    public ResponseEntity<ResponseMetaData> basicRegister(@RequestBody BasicAuthRequest request) {
        return authService.basicAuth(request,
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
