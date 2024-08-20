package com.ing.hubs.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.ttlInMinutes}")
    private int ttlInMinutes;

    public String extractUsername(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getJwtKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
            log.error("Error extracting username from token", e);
            return null;
        }
    }

    public String generateJwt(UserDetails userDetails) {
        Date expirationDateTime = Date.from(ZonedDateTime.now().plusMinutes(ttlInMinutes).toInstant());

        var roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return Jwts
                .builder()
                .subject(userDetails.getUsername())
                .claim("roles", roles)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expirationDateTime)
                .signWith(getJwtKey())
                .compact();
    }

    private SecretKey getJwtKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}