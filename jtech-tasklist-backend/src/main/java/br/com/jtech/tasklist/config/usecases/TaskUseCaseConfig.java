package br.com.jtech.tasklist.config.usecases;

import br.com.jtech.tasklist.adapters.output.TaskAdapter;
import br.com.jtech.tasklist.application.core.usecases.TaskUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskUseCaseConfig {

    @Bean
    public TaskUseCase taskUseCase(TaskAdapter taskAdapter) {
        return new TaskUseCase(taskAdapter);
    }
}
