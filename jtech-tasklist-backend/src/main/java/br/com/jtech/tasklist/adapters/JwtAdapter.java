package br.com.jtech.tasklist.adapters;

import br.com.jtech.tasklist.config.infra.properties.JwtProperties;
import br.com.jtech.tasklist.config.infra.security.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtAdapter implements JwtService {

    private final JwtProperties properties;
    private final SecretKey key;

    public JwtAdapter(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(
                properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + properties.getExpiration());

        return Jwts.builder()
                .subject(userId)
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    @Override
    public boolean isValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getSubject(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    @Override
    public String getUserId(String token) {
        return getSubject(token);
    }

    @Override
    public String refreshToken(String token) {
        String userId = getSubject(token);
        return generateToken(userId);
    }
}