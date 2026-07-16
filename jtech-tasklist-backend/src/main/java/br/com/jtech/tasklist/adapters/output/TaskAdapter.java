package br.com.jtech.tasklist.adapters.output;

import br.com.jtech.tasklist.adapters.output.repositories.TaskRepository;
import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.ports.output.TaskOutputGateway;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TaskAdapter implements TaskOutputGateway {

    private final TaskRepository taskRepository;

    public TaskAdapter(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task save(Task task) {
        TaskEntity entity = task.toEntity();
        TaskEntity saved = taskRepository.save(entity);
        return Task.of(saved);
    }

    @Override
    public Optional<Task> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return taskRepository.findById(UUID.fromString(id)).map(Task::of);
    }

    @Override
    public void deleteById(String id) {
        if (id != null) {
            taskRepository.deleteById(UUID.fromString(id));
        }
    }

    @Override
    public List<Task> findAllByGroupId(String groupId) {
        if (groupId == null) {
            return List.of();
        }
        List<TaskEntity> entities = taskRepository.findByGroupId(UUID.fromString(groupId));
        return entities.stream().map(Task::of).toList();
    }
}
