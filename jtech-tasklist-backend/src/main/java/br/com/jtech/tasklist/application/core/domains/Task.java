package br.com.jtech.tasklist.application.core.domains;

import java.time.LocalDateTime;
import java.util.UUID;

public class Task {

    private UUID id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private TaskGroup group;
    private TaskStatusEnum status;
    public Task(UUID id, String name, String description, LocalDateTime createdAt,  TaskGroup group, TaskStatusEnum status) {
        this.id = id;
    }
}
