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

    public Integer getUserAttendanceId() {
        Object attendanceId = extractPrincipal().getClaim("attendanceId");
        if (attendanceId instanceof Long longVal)
            return longVal.intValue();
        else if (attendanceId instanceof String stringVal)
            return Integer.parseInt(stringVal);
        return null;
    }

    public String getDirectLead() {
        return extractPrincipal().getClaimAsString("directLead");
    }

    // SecurityContextHolder.getContext().getAuthentication() could not be null
    // since to get to this method authentication must have been set
    private Jwt extractPrincipal() {
        return ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

}
