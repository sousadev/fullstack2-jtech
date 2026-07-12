package br.com.jtech.tasklist.config.usecases;

import br.com.jtech.tasklist.application.core.usecases.TaskGroupUseCase;
import br.com.jtech.tasklist.application.ports.output.TaskGroupOutputGateway;
import br.com.jtech.tasklist.config.infra.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskGroupUseCaseConfig {
    @Bean
    public TaskGroupUseCase taskGroupUseCase(JwtService jwtService, TaskGroupOutputGateway gateway) {
        return new TaskGroupUseCase(jwtService, gateway);
    }
}
