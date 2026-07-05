package br.com.jtech.tasklist.adapters.input.protocols;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.jtech.tasklist.application.core.domains.TaskGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskGroupResponse implements Serializable {

    private String id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskGroupResponse of(TaskGroup taskGroup) {
        return TaskGroupResponse.builder()
                .id(taskGroup.getId())
                .name(taskGroup.getName())
                .description(taskGroup.getDescription())
                .createdAt(taskGroup.getCreatedAt())
                .updatedAt(taskGroup.getUpdatedAt().orElse(null))
                .build();
    }

    public static List<TaskGroupResponse> of(List<TaskGroup> taskGroups) {
        return taskGroups.stream().map(TaskGroupResponse::of).toList();
    }

}
