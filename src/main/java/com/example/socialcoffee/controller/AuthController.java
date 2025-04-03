package com.example.socialcoffee.controller;

import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/url")
    public ResponseEntity<ResponseMetaData> auth(@RequestParam("redirect_uri") String redirectUri) {
        return authService.getGoogleAuthorizationCodeRequestUrl(redirectUri);
    }

    @GetMapping("/callback")
    public ResponseEntity<ResponseMetaData> callback(@RequestParam("code") String code,
                                                     @RequestParam("redirect_uri") String redirectUri) {
        return authService.getJwtToken(code,
                                       redirectUri);
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
