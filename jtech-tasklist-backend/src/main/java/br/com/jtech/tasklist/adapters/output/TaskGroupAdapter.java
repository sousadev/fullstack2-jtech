package br.com.jtech.tasklist.adapters.output;

import br.com.jtech.tasklist.adapters.input.protocols.TaskGroupResponse;
import br.com.jtech.tasklist.adapters.input.protocols.TaskResponse;
import br.com.jtech.tasklist.adapters.output.repositories.TaskGroupRepository;
import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskGroupEntity;
import br.com.jtech.tasklist.application.core.domains.TaskGroup;
import br.com.jtech.tasklist.application.ports.output.TaskGroupOutputGateway;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class TaskGroupAdapter implements TaskGroupOutputGateway {

    private final TaskGroupRepository taskGroupRepository;

    public TaskGroupAdapter(TaskGroupRepository taskGroupRepository) {
        this.taskGroupRepository = taskGroupRepository;
    }

    @Override
    public TaskGroupResponse create(String userId, TaskGroup taskGroup) {
        TaskGroupEntity entity = TaskGroupEntity.toEntity(taskGroup);
        TaskGroupEntity saved = taskGroupRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public TaskGroupResponse update(String id, TaskGroup taskGroup) {
        if (id == null) {
            return null;
        }

        TaskGroupEntity entity = taskGroupRepository.findById(UUID.fromString(id)).orElse(null);
        if (entity == null) {
            return null;
        }

        entity.setName(taskGroup.getName());
        entity.setDescription(taskGroup.getDescription());
        TaskGroupEntity saved = taskGroupRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public TaskGroupResponse delete(String id) {
        if (id == null) {
            return null;
        }

        TaskGroupEntity entity = taskGroupRepository.findById(UUID.fromString(id)).orElse(null);
        if (entity == null) {
            return null;
        }

        taskGroupRepository.delete(entity);
        return toResponse(entity);
    }

    @Override
    public TaskGroupResponse findById(String id) {
        if (id == null) {
            return null;
        }

        TaskGroupEntity entity = taskGroupRepository.findById(UUID.fromString(id)).orElse(null);
        return toResponse(entity);
    }

    @Override
    public List<TaskGroupResponse> findAllByUser(String user_id) {
        if (user_id == null) {
            return List.of();
        }
        List<TaskGroupEntity> entities = taskGroupRepository.findByUserId(UUID.fromString(user_id));
        return entities.stream().map(this::toResponse).toList();
    }

    private TaskGroupResponse toResponse(TaskGroupEntity entity) {
        if (entity == null) {
            return null;
        }
        return TaskGroupResponse.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .name(entity.getName())
                .description(entity.getDescription())
                .user_id(entity.getUser() != null && entity.getUser().getId() != null
                        ? entity.getUser().getId().toString()
                        : null)
                .created_at(entity.getCreatedAt())
                .tasks(entity.getTasks() != null ? entity.getTasks().stream().map(this::toTaskResponse).toList()
                        : List.of())
                .build();
    }

    private TaskResponse toTaskResponse(TaskEntity entity) {
        if (entity == null) {
            return null;
        }
        return TaskResponse.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .group_id(entity.getGroup() != null && entity.getGroup().getId() != null
                        ? entity.getGroup().getId().toString()
                        : null)
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .created_at(entity.getCreatedAt())
                .build();
    }
}
