package br.com.jtech.tasklist.application.core.domains;

import br.com.jtech.tasklist.adapters.input.protocols.TaskGroupRequest;
import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskGroupEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskGroup {
    private String id;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    private Optional<LocalDateTime> updatedAt;

    private Task tasks;

    public static List<TaskGroup> of(List<TaskGroupEntity> entities) {
        return entities.stream().map(TaskGroup::of).toList();
    }

    public TaskGroupEntity toEntity(TaskGroupEntity entity) {
        return TaskGroupEntity.builder()
                .id(UUID.fromString(getId()))
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static TaskGroup of(TaskGroupEntity entity) {
        return TaskGroup.builder()
                .id(entity.getId().toString())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(Optional.ofNullable(entity.getUpdatedAt()))
                .build();
    }

    public static TaskGroup of(TaskGroupRequest request) {
        return TaskGroup.builder()
                .id(request.getId())
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
    }

}
