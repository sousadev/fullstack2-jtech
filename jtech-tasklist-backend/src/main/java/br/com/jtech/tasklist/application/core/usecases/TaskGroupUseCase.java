package br.com.jtech.tasklist.application.core.usecases;

import java.util.List;

import br.com.jtech.tasklist.adapters.output.TaskGroupAdapter;
import br.com.jtech.tasklist.application.core.domains.TaskGroup;
import br.com.jtech.tasklist.application.ports.input.TaskGroupInputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskGroupOutputGateway;

public class TaskGroupUseCase implements TaskGroupInputGateway {

    public final TaskGroupOutputGateway taskGroupOutputGateway;

    public TaskGroupUseCase(TaskGroupAdapter taskGroupAdapter) {
        this.taskGroupOutputGateway = taskGroupAdapter;
    }

    public TaskGroup create(TaskGroup taskGroup) {
        return taskGroupOutputGateway.create(taskGroup);
    }

    public TaskGroup findById(String id) {
        return taskGroupOutputGateway.findById(id);
    }

    public List<TaskGroup> findAll() {
        return taskGroupOutputGateway.findAll();
    }
}
