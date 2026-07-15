package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.adapters.input.protocols.TaskGroupRequest;
import br.com.jtech.tasklist.adapters.input.protocols.TaskGroupResponse;
import br.com.jtech.tasklist.application.core.domains.TaskGroup;
import br.com.jtech.tasklist.application.ports.input.TaskGroupInputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskGroupOutputGateway;
import br.com.jtech.tasklist.config.infra.security.JwtService;

import java.util.List;

public class TaskGroupUseCase implements TaskGroupInputGateway {

    private final JwtService jwtService;
    private final TaskGroupOutputGateway gateway;

    public TaskGroupUseCase(JwtService jwtService, TaskGroupOutputGateway gateway) {
        this.jwtService = jwtService;
        this.gateway = gateway;
    }

    @Override
    public TaskGroupResponse create(String token, TaskGroupRequest taskGroupRequest) {
        String userId = jwtService.isValid(token) ? jwtService.getUserId(token) : null;
        System.out.println("User "+ userId);
        taskGroupRequest.setUser_id(userId);

        TaskGroup taskGroup = TaskGroup.of(taskGroupRequest);
        return gateway.create(userId, taskGroup);
    }

    @Override
    public TaskGroupResponse update(TaskGroup taskGroup) {
        return null;
    }

    @Override
    public TaskGroupResponse delete(TaskGroup taskGroup) {
        return null;
    }

    @Override
    public TaskGroupResponse findById(String id) {
        return null;
    }

    @Override
    public List<TaskGroupResponse> findAllByUser(String token) {
        String userId = jwtService.isValid(token) ? jwtService.getSubject(token) : null;
        if (userId == null) {
            return List.of();
        }
        return gateway.findAllByUser(userId);
    }
}
