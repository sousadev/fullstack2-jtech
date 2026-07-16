package br.com.jtech.tasklist.application.core.domains;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.jtech.tasklist.adapters.input.protocols.TaskRequest;
import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskGroupEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    private String id;
    private String groupId;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private TaskStatusEnum status;

    public static Task of(TaskRequest request) {
        return Task.builder()
                .groupId(request.getGroup_id())
                .name(request.getName())
                .description(request.getDescription())
                .status(request.getStatus() != null ? TaskStatusEnum.valueOf(request.getStatus()) : TaskStatusEnum.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Task of(TaskEntity entity) {
        return Task.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .groupId(entity.getGroup() != null && entity.getGroup().getId() != null ? entity.getGroup().getId().toString() : null)
                .build();
    }

    public TaskEntity toEntity() {
        return TaskEntity.builder()
                .id(id != null ? UUID.fromString(id) : null)
                .name(name)
                .description(description)
                .status(status)
                .createdAt(createdAt)
                .group(groupId != null ? TaskGroupEntity.builder().id(UUID.fromString(groupId)).build() : null)
                .build();
    }
}
