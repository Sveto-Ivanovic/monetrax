package com.monetrax.monetrax.auth.security;

import com.monetrax.monetrax.auth.dto.AuthInfo;
import com.monetrax.monetrax.auth.exceptions.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("jwt.secret")
    private String jwtSecret;

    @Value("jwt.expiration")
    private long jwtExpiration;

    private SecretKey key;

    @PostConstruct
    public void init(){
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, UUID userId){
        return Jwts.builder()
                .subject(email)
                .claim("type", "access")
                .claim("userId", userId.toString())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime()+ jwtExpiration))
                .signWith(this.key)
                .compact();
    }


    public AuthInfo getAuthInfoFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new JwtAuthenticationException("Invalid or expired token.");
        }

        String uuidString = claims.get("userId", String.class);
        String type = claims.get("type", String.class);
        String email = claims.getSubject();
        if (uuidString == null || type == null || email == null) {
            throw new JwtAuthenticationException("Token missing userId, type or email claim.");
        }

        try {
            return AuthInfo.builder()
                    .userId(UUID.fromString(uuidString))
                    .email(email)
                    .type(type)
                    .build();
        } catch (IllegalArgumentException e) {
            throw new JwtAuthenticationException("Invalid userId format in token");
        }


    }



}
