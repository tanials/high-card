package it.sara.demo.service.util;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final int expirationMinutes;
    private final String issuer;

    public JwtUtil(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.expiration}") int expirationMinutes,
        @Value("${jwt.issuer}") String issuer
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMinutes = expirationMinutes;
        this.issuer = issuer;
    }

    public String generateToken(String username, String role) {

        Instant expiry = Instant.now().plus(Duration.ofMinutes(expirationMinutes));

        return Jwts.builder()
            .setSubject(username)
            .setIssuer(issuer)
            .claim("role", role)
            .setExpiration(Date.from(expiry))
            .signWith(secretKey)
            .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .requireIssuer(issuer)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

}
