package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.adapters.output.TaskAdapter;
import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.ports.input.TaskInputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskOutputGateway;

import java.util.List;

public class TaskUseCase implements TaskInputGateway {

    public final TaskOutputGateway taskOutputGateway;

    public TaskUseCase(TaskAdapter taskAdapter) {
        this.taskOutputGateway = taskAdapter;
    }

    public Task create(Task task) {
        return taskOutputGateway.create(task);
    };

    public Task findById(String id) {
        return taskOutputGateway.findById(id);
    };

    public Task update(Task task) {
        return taskOutputGateway.update(task);
    };

    public Task delete(Task task) {
        taskOutputGateway.delete(task);
        return task;
    }

    public List<Task> findAll() {
        return taskOutputGateway.findAll();
    }

}
