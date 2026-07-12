package br.com.jtech.tasklist.adapters.output.repositories.entities;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.domains.TaskGroup;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity(name = "task_group")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String description;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskEntity> tasks;

    public static TaskGroupEntity toEntity(TaskGroup taskGroup) {
        if (taskGroup == null) {
            return null;
        }
        return TaskGroupEntity.builder()
                .id(taskGroup.getId() != null ? UUID.fromString(taskGroup.getId()) : null)
                .name(taskGroup.getName())
                .description(taskGroup.getDescription())
                .createdAt(taskGroup.getCreatedAt())
                .user(taskGroup.getUser_id() != null ? UserEntity.builder().id(UUID.fromString(taskGroup.getUser_id())).build() : null)
                .tasks(taskGroup.getTasks() != null ? taskGroup.getTasks().stream().map(Task::toEntity).toList() : null)
                .build();
    }

    public TaskGroup toDomain() {
        return TaskGroup.builder()
                .id(id != null ? id.toString() : null)
                .name(name)
                .description(description)
                .createdAt(createdAt)
                .user_id(user != null && user.getId() != null ? user.getId().toString() : null)
                .tasks(tasks != null ? tasks.stream().map(Task::of).toList() : null)
                .build();
    }

}
