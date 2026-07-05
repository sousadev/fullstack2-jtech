package br.com.jtech.tasklist.application.core.domains;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import br.com.jtech.tasklist.adapters.input.protocols.TaskRequest;
import br.com.jtech.tasklist.adapters.output.repositories.enums.TaskStatusEnum;
import lombok.Builder;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    private String id;
    private String name;
    private String description;

    private TaskGroup group;

    private TaskStatusEnum status;

    private LocalDateTime createdAt;

    private Optional<LocalDateTime> updatedAt;

    public static Task of(TaskRequest request) {
        return Task.builder()
                .id(request.getId())
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
    }

}
