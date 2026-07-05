package br.com.jtech.tasklist.adapters.output;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.ports.output.TaskOutputGateway;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TaskAdapter implements TaskOutputGateway {

    @Override
    public Task create(Task task) {
        return task;
    }
    @Override
    public Task update(Task task) {
        return task;
    }

    @Override
    public void delete(Task task) {

    }

    @Override
    public Task findById(String id) {
        return null;
    }

    @Override
    public List<Task> findAll() {
        return List.of();
    }
}
