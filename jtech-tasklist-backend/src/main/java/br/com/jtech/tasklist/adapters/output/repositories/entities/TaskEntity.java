package br.com.jtech.tasklist.adapters.output.repositories.entities;

import br.com.jtech.tasklist.application.core.domains.TaskStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity(name = "task")
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String description;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private TaskGroupEntity group;

    @Enumerated(EnumType.STRING)
    private TaskStatusEnum status;

}
