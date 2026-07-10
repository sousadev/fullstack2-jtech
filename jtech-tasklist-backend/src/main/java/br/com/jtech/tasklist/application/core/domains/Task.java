package br.com.jtech.tasklist.application.core.domains;

import java.time.LocalDateTime;
import java.util.UUID;

public class Task {

    private String id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private TaskStatusEnum status;

    public static Task of(TaskRequest request){

        return Task.builder()
                .id(request.id())
                .name(request.name())
                .description(request.description())
                .build();

    }

    public static Task of(TaskEntity entity){

        return Task.builder()
                .id(entity.getId().toString())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();

    }

    public TaskEntity toEntity(){

        return TaskEntity.builder()
                .id(UUID.fromString(id))
                .name(name)
                .description(description)
                .status(status)
                .createdAt(createdAt)
                .build();

    }
}
