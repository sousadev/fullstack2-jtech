package br.com.jtech.tasklist.application.ports.input;

import br.com.jtech.tasklist.adapters.input.protocols.TaskRequest;
import br.com.jtech.tasklist.adapters.input.protocols.TaskResponse;

import java.util.List;

public interface TaskInputGateway {
    TaskResponse create(String token, TaskRequest request);

    TaskResponse update(String token, String taskId, TaskRequest request);

    void delete(String token, String taskId);

    TaskResponse findById(String token, String taskId);

    List<TaskResponse> findAllByGroup(String token, String groupId);
}
