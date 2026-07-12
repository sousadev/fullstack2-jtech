package br.com.jtech.tasklist.application.core.domains;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.jtech.tasklist.adapters.input.protocols.TaskGroupRequest;
import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskGroupEntity;

import java.util.List;

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
public class TaskGroup {

    public String id;
    public String name;
    public String description;
    public LocalDateTime createdAt;
    public List<Task> tasks;
    public String user_id;

    public static TaskGroup of(TaskGroupRequest request) {
        return TaskGroup.builder()
                .name(request.getName())
                .description(request.getDescription())
                .user_id(request.getUser_id())
                .build();
    }

    public static TaskGroup of(TaskGroupEntity entity) {
        if (entity == null) {
            return null;
        }
        return TaskGroup.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .user_id(entity.getUser() != null && entity.getUser().getId() != null ? entity.getUser().getId().toString() : null)
                .tasks(entity.getTasks() != null ? entity.getTasks().stream().map(Task::of).toList() : null)
                .build();
    }

    public TaskGroupEntity toEntity() {
        return TaskGroupEntity.builder()
                .id(id != null ? UUID.fromString(id) : null)
                .name(name)
                .description(description)
                .createdAt(createdAt)
                .build();
    }
}
