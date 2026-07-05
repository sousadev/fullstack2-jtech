package br.com.jtech.tasklist.config.usecases;

import br.com.jtech.tasklist.adapters.output.TaskGroupAdapter;
import br.com.jtech.tasklist.application.core.usecases.TaskGroupUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskGroupUseCaseConfig {

    @Bean
    public TaskGroupUseCase taskGroupUseCase(TaskGroupAdapter taskGroupAdapter) {
        return new TaskGroupUseCase(taskGroupAdapter);
    }
}
