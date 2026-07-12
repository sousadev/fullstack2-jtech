package br.com.jtech.tasklist.application.ports.input;

import br.com.jtech.tasklist.adapters.input.protocols.TaskGroupRequest;
import br.com.jtech.tasklist.adapters.input.protocols.TaskGroupResponse;
import br.com.jtech.tasklist.application.core.domains.TaskGroup;

import java.util.List;

public interface TaskGroupInputGateway {
    TaskGroupResponse create(String token, TaskGroupRequest taskGroup);

    TaskGroupResponse update(TaskGroup taskGroup);

    TaskGroupResponse delete(TaskGroup taskGroup);

    TaskGroupResponse findById(String id);

    List<TaskGroupResponse> findAllByUser(String user_id);
}
