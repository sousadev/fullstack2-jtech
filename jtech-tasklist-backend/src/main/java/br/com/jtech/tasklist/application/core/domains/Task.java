package br.com.jtech.tasklist.application.core.domains;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.jtech.tasklist.adapters.input.protocols.TaskRequest;
import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import lombok.Builder;

@Builder
public class Task {

    private String id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private TaskStatusEnum status;

    public static Task of(TaskRequest request) {

        return Task.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

    }

    public static Task of(TaskEntity entity) {

        return Task.builder()
                .id(entity.getId().toString())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();

    }

    public TaskEntity toEntity() {

        return TaskEntity.builder()
                .id(id != null ? UUID.fromString(id) : null)
                .name(name)
                .description(description)
                .status(status)
                .createdAt(createdAt)
                .build();

    }
}
