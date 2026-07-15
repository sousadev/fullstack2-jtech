package br.com.jtech.tasklist.config.infra.security;

public interface JwtService {

    String generateToken(String userId);

    boolean isValid(String token);

    String getSubject(String token);

    String refreshToken(String token);

    String getUserId(String token);
}