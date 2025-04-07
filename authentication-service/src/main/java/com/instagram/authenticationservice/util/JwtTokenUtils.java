package com.instagram.authenticationservice.util;

import com.instagram.authenticationservice.dto.kafka.UserAuthenticationDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtils {

    @Value("${jwt.user_secret}")
    private String userSecret;

    @Value("${jwt.user_secret_lifetime}")
    private Duration userSecretLifetime;

    public String generateToken(UserAuthenticationDto userAuthenticationDto) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userAuthenticationDto.username());

        Date issuedAt = new Date();
        Date expirationDate = new Date(issuedAt.getTime() + userSecretLifetime.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userAuthenticationDto.username())
                .setIssuedAt(issuedAt)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, userSecret)
                .compact();
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(userSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isValidToken(String token) {
        try {
            getAllClaimsFromToken(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) { return getAllClaimsFromToken(token).getSubject(); }
}
