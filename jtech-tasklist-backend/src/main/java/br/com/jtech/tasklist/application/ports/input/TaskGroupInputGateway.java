package br.com.jtech.tasklist.application.ports.input;

import br.com.jtech.tasklist.application.core.domains.TaskGroup;
import java.util.List;

public interface TaskGroupInputGateway {
    TaskGroup create(TaskGroup taskGroup);

    TaskGroup findById(String id);

    List<TaskGroup> findAll();
}
