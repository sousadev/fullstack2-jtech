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
        System.out.println("User " + userId);
        taskGroupRequest.setUser_id(userId);

        TaskGroup taskGroup = TaskGroup.of(taskGroupRequest);
        return gateway.create(userId, taskGroup);
    }

    @Override
    public TaskGroupResponse findById(String id, String token) {
        String userId = jwtService.isValid(token) ? jwtService.getUserId(token) : null;
        if (userId == null) {
            return null;
        }

        TaskGroupResponse group = gateway.findById(id);
        if (group == null || !userId.equals(group.getUser_id())) {
            return null;
        }
        return group;
    }

    @Override
    public List<TaskGroupResponse> findAllByUser(String token) {
        String userId = jwtService.isValid(token) ? jwtService.getSubject(token) : null;
        if (userId == null) {
            return List.of();
        }
        return gateway.findAllByUser(userId);
    }

    @Override
    public TaskGroupResponse update(String id, TaskGroupRequest taskGroupRequest, String token) {
        String userId = jwtService.isValid(token) ? jwtService.getUserId(token) : null;
        if (userId == null) {
            return null;
        }

        TaskGroupResponse existing = gateway.findById(id);
        if (existing == null || !userId.equals(existing.getUser_id())) {
            return null;
        }

        taskGroupRequest.setUser_id(userId);
        TaskGroup taskGroup = TaskGroup.of(taskGroupRequest);
        return gateway.update(id, taskGroup);
    }

    @Override
    public TaskGroupResponse delete(String id, String token) {
        String userId = jwtService.isValid(token) ? jwtService.getUserId(token) : null;
        if (userId == null) {
            return null;
        }

        TaskGroupResponse existing = gateway.findById(id);
        if (existing == null || !userId.equals(existing.getUser_id())) {
            return null;
        }

        return gateway.delete(id);
    }
}
