package br.com.jtech.tasklist.application.core.domains;

import java.time.LocalDateTime;
import java.util.UUID;

public class TaskGroup {

    public UUID id;
    public String name;
    public String description;
    public LocalDateTime createdAt;
    public TaskGroup() {
    }
    public TaskGroup(UUID id, String name, String description,  LocalDateTime createdAt) {
        this.id = id;
    }
}
