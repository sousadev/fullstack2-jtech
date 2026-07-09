package br.com.jtech.tasklist.config.usecases;

import br.com.jtech.tasklist.adapters.output.AuthAdapter;
import br.com.jtech.tasklist.application.core.usecases.AuthUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthUseCaseConfig {
    @Bean
    public AuthUseCase authUseCase(AuthAdapter authAdapter) {
        return new AuthUseCase(authAdapter);
    }
}
