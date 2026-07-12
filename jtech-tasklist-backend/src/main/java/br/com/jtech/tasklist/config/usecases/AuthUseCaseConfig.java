package br.com.jtech.tasklist.config.usecases;

import br.com.jtech.tasklist.adapters.output.AuthAdapter;
import br.com.jtech.tasklist.application.core.usecases.AuthUseCase;
import br.com.jtech.tasklist.config.infra.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthUseCaseConfig {
    @Bean
    public AuthUseCase authUseCase(AuthAdapter authAdapter, JwtService jwtService, PasswordEncoder passwordEncoder) {
        return new AuthUseCase(authAdapter, jwtService, passwordEncoder);
    }
}
