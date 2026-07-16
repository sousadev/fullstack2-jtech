package br.com.jtech.tasklist.config.usecases;

import br.com.jtech.tasklist.application.core.usecases.TaskUseCase;
import br.com.jtech.tasklist.application.ports.output.TaskOutputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskGroupOutputGateway;
import br.com.jtech.tasklist.config.infra.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskUseCaseConfig {
    @Bean
    public TaskUseCase taskUseCase(JwtService jwtService, TaskOutputGateway taskOutputGateway, TaskGroupOutputGateway taskGroupOutputGateway) {
        return new TaskUseCase(jwtService, taskOutputGateway, taskGroupOutputGateway);
    }
}
