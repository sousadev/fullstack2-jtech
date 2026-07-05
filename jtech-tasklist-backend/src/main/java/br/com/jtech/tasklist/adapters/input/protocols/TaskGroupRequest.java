package br.com.jtech.tasklist.adapters.input.protocols;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import br.com.jtech.tasklist.adapters.output.repositories.enums.TaskStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

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
public class TaskGroupRequest implements Serializable {
    private String id;
    private String name;
    private String description;
    private TaskStatusEnum status;
    private LocalDateTime createdAt;
    private Optional<LocalDateTime> updatedAt;
    private List<TaskGroupRequest> children;
}
