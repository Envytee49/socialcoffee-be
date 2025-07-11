package com.example.socialcoffee.service;

import com.example.socialcoffee.domain.postgres.Role;
import com.example.socialcoffee.domain.postgres.User;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static com.example.socialcoffee.constants.CommonConstant.REFRESH_TOKEN_PREFIX;
import static com.example.socialcoffee.constants.CommonConstant.TOKEN_PREFIX;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {
    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length-in-seconds}")
    private long expireLengthInSeconds;

    @Value("${security.jwt.refresh-token.expire-length-in-seconds}")
    private long refreshExpireLengthInSeconds;

    public String generateAccessToken(final User user) {
        JWTClaimsSet jwtClaimsSet = buildBaseJwtClaimsSet(user, expireLengthInSeconds)
                .claim("scope", getRoleList(user))
                .build();

        return generateToken(jwtClaimsSet, TOKEN_PREFIX, expireLengthInSeconds);
    }

    private JWTClaimsSet.Builder buildBaseJwtClaimsSet(User user, long expireLength) {
        return new JWTClaimsSet.Builder()
                .issueTime(new Date())
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .expirationTime(Date.from(Instant.now().plusSeconds(expireLength)));
    }

    private List<String> getRoleList(User user) {
        return user.getRoles()
                .stream()
                .map(Role::getName)
                .toList();
    }

    public String generateToken(JWTClaimsSet jwtClaimsSet, String tokenPrefix, long expireLength) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            JWSSigner signer = new MACSigner(secretKey);
            jwsObject.sign(signer);
            String token = jwsObject.serialize();
            return token;
        } catch (JOSEException e) {
            throw new JwtException(e.getMessage());
        }
    }

    public String generateRefreshToken(final User user) {
        JWTClaimsSet jwtClaimsSet = buildBaseJwtClaimsSet(user, refreshExpireLengthInSeconds).build();
        return generateToken(jwtClaimsSet, REFRESH_TOKEN_PREFIX, refreshExpireLengthInSeconds);
    }

    public SignedJWT verifyToken(String token, String tokenPrefix) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(secretKey);
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
        Date expirationTime = jwtClaimsSet.getExpirationTime();
        boolean isVerified = signedJWT.verify(verifier);

        if (!(isVerified && expirationTime.after(new Date()))) {
            throw new RuntimeException("Invalid Token");
        }
        return signedJWT;
    }
}
