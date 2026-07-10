package br.com.jtech.tasklist.application.core.domains;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

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

    public static TaskGroup of(TaskTGroupRequest request) {
        return TaskGroup.builder()
                        .id(request.id())
                        .name(request.name())
                        .description(request.description())
                        .build();
    }

    public statid TaskGroup of(TaskGroupEntity entity) {
        return TaskGroup.builder()
                        .id(entity.getId().toString())
                        .name(entity.getName())
                        .description(entity.getDescription())
                        .createdAt(entity.getCreatedAt())
                        .build();
    }

    public TaskGroupEntity toEntity() {
        return TaskGroup.builder()
                        .id(UUID.fromString(id))
                        .name(name)
                        .description(description)
                        .createdAt(createdAt)
                        .build();
    }
}
