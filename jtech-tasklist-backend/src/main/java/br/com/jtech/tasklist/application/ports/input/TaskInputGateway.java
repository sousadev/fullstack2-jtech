package br.com.jtech.tasklist.application.ports.input;

import br.com.jtech.tasklist.adapters.input.protocols.TaskResponse;
import br.com.jtech.tasklist.application.core.domains.User;

import java.util.List;

public interface TaskInputGateway {
    TaskResponse create(User user);
    TaskResponse update(User user);
    TaskResponse delete(User user);
    TaskResponse findById(String id);
    List<TaskResponse> findAllByTaskGroup(String taskgroup_id);
}
