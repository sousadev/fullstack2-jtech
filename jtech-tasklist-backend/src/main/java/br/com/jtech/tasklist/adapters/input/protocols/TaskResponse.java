package br.com.jtech.tasklist.adapters.input.protocols;

import br.com.jtech.tasklist.adapters.output.repositories.enums.TaskStatusEnum;
import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.domains.TaskGroup;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskResponse implements Serializable {

    private String id;
    private String name;
    private String description;
    private TaskStatusEnum status;
    private LocalDateTime createdAt;
    private Optional<LocalDateTime> updatedAt;
    private TaskGroup group;

    public static TaskResponse of(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .group(task.getGroup())
                .build();
    }

    public static List<TaskResponse> of(List<Task> tasks) {
        return tasks.stream().map(TaskResponse::of).toList();
    }
}
