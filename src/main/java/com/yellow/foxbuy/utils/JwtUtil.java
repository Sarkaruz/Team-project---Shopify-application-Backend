package com.yellow.foxbuy.utils;

import com.yellow.foxbuy.models.User;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;

import java.time.temporal.ChronoUnit;

import java.util.Base64;
import java.util.Date;


@Component
public class JwtUtil {

    String secret = System.getenv("JWT_SECRET_KEY");

    Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.getJcaName());

    public String createToken(User user) {
        Instant now = Instant.now();
        String jwtToken = Jwts.builder()
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(3000, ChronoUnit.MINUTES)))
                .signWith(hmacKey)
                .compact();
        return jwtToken;
    }


    public Jws<Claims> validateJwt(String jwtString) {
        Jws<Claims> jwt = Jwts.parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(jwtString);
        return jwt;
    }

    public String getUsernameFromJWT(String token) {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return (String) claims.get("username");
    }
    public Date extractExpiration(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }
}