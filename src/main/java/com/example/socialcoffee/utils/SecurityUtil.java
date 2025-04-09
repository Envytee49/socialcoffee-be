package com.example.socialcoffee.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@UtilityClass
@Slf4j
public class SecurityUtil {

    public String getUserEmail() {
        return extractPrincipal().getSubject();
    }

    public Long getUserId() {
        return Long.parseLong(extractPrincipal().getClaim("userId"));
    }

    public String getUserRole() {
        return extractPrincipal().getClaimAsString("role");
    }

    // SecurityContextHolder.getContext().getAuthentication() could not be null
    // since to get to this method authentication must have been set
    private Jwt extractPrincipal() {
        return ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

}
