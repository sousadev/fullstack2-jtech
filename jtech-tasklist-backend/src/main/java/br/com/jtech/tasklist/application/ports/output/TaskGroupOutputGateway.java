package br.com.jtech.tasklist.application.ports.output;

import java.util.List;

import br.com.jtech.tasklist.application.core.domains.TaskGroup;

public interface TaskGroupOutputGateway {
    TaskGroup create(TaskGroup taskGroup);

    TaskGroup findById(String id);

    List<TaskGroup> findAll();

}
