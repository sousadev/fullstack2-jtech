package br.com.jtech.tasklist.application.ports.output;

import br.com.jtech.tasklist.application.core.domains.Task;

import java.util.List;
import java.util.Optional;

public interface TaskOutputGateway {
    Task save(Task task);

    Optional<Task> findById(String id);

    void deleteById(String id);

    List<Task> findAllByGroupId(String groupId);
}
