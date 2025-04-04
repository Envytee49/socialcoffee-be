package com.example.socialcoffee.configuration.security;

import com.example.socialcoffee.service.JwtService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;

import static com.example.socialcoffee.constants.CommonConstant.TOKEN_PREFIX;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomJwtDecoder implements JwtDecoder {
    private final JwtService jwtService;
    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            jwtService.verifyToken(token, TOKEN_PREFIX);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HS512");
            NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
            return jwtDecoder.decode(token);
        } catch (JOSEException | ParseException e) {
            log.error("401 Unauthenticated: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (JwtException e) {
            throw new JwtException("Token decoding error", e);
        }
    }
}
