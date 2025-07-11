package com.example.socialcoffee.controller;

import com.example.socialcoffee.domain.postgres.User;
import com.example.socialcoffee.dto.request.*;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.AuthAction;
import com.example.socialcoffee.service.AuthService;
import com.example.socialcoffee.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController extends BaseController {

    private final AuthService authService;
    private final UserService userService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/validate")
    public ResponseEntity<ResponseMetaData> validate() {
        User user = getCurrentUser();
        return userService.getMyProfile(user);
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
    public ResponseEntity<ResponseMetaData> basicLogin(@Valid @RequestBody LoginRequest request) {
        return authService.basicLogin(request);
    }

    @PostMapping("/basic/register")
    public ResponseEntity<ResponseMetaData> basicRegister(@Valid @RequestBody RegisterRequest request) {
        return authService.basicRegister(request);
    }

    @PutMapping("/users/update-password")
    public ResponseEntity<ResponseMetaData> updatePassword(@RequestBody UpdateNewPassword updateNewPassword) {
        return authService.updateNewPassword(updateNewPassword);
    }
}
