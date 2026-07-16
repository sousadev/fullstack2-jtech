package br.com.jtech.tasklist.application.ports.output;

import br.com.jtech.tasklist.adapters.input.protocols.TaskGroupResponse;
import br.com.jtech.tasklist.application.core.domains.TaskGroup;

import java.util.List;

public interface TaskGroupOutputGateway {
    TaskGroupResponse create(String userId, TaskGroup taskGroup);

    TaskGroupResponse update(String id, TaskGroup taskGroup);

    TaskGroupResponse delete(String id);

    TaskGroupResponse findById(String id);

    List<TaskGroupResponse> findAllByUser(String userId);
}
