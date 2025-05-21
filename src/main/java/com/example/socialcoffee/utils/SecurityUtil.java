package com.example.socialcoffee.utils;

import com.example.socialcoffee.domain.Role;
import com.example.socialcoffee.enums.RoleEnum;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

@UtilityClass
@Slf4j
public class SecurityUtil {

    public String getUserEmail() {
        return extractPrincipal().getSubject();
    }

    public Long getUserId() {
        return NumberUtils.toLong(extractPrincipal().getClaimAsString("userId"), NumberUtils.LONG_ZERO);
    }

    public List<String> getUserRole() {
        return extractPrincipal().getClaimAsStringList("scope");
    }

    // SecurityContextHolder.getContext().getAuthentication() could not be null
    // since to get to this method authentication must have been set
    private Jwt extractPrincipal() {
        return ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    public boolean isAuthenticated() {
        return !(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String);
    }

    public static boolean isAdmin() {
        return getUserRole().contains(RoleEnum.ADMIN.getValue());
    }
}
