package br.com.jtech.tasklist.application.ports.output;

import br.com.jtech.tasklist.application.core.domains.Task;

import java.util.List;

public interface TaskOutputGateway {
    Task create(Task task);

    Task update(Task task);

    void delete(Task task);

    Task findById(String id);

    List<Task> findAll();
}
