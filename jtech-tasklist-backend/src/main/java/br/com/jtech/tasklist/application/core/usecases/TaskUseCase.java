package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.adapters.input.protocols.TaskRequest;
import br.com.jtech.tasklist.adapters.input.protocols.TaskResponse;
import br.com.jtech.tasklist.adapters.input.protocols.TaskGroupResponse;
import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.domains.TaskStatusEnum;
import br.com.jtech.tasklist.application.ports.input.TaskInputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskOutputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskGroupOutputGateway;
import br.com.jtech.tasklist.config.infra.security.JwtService;

import java.util.List;
import java.util.Optional;

public class TaskUseCase implements TaskInputGateway {

    private final JwtService jwtService;
    private final TaskOutputGateway taskOutputGateway;
    private final TaskGroupOutputGateway taskGroupOutputGateway;

    public TaskUseCase(JwtService jwtService, TaskOutputGateway taskOutputGateway,
            TaskGroupOutputGateway taskGroupOutputGateway) {
        this.jwtService = jwtService;
        this.taskOutputGateway = taskOutputGateway;
        this.taskGroupOutputGateway = taskGroupOutputGateway;
    }

    @Override
    public TaskResponse create(String token, TaskRequest request) {
        String userId = jwtService.isValid(token) ? jwtService.getUserId(token) : null;
        if (userId == null || request.getGroup_id() == null) {
            return null;
        }

        Task task = Task.of(request);
        Task savedTask = taskOutputGateway.save(task);
        return toResponse(savedTask);
    }

    @Override
    public TaskResponse update(String token, String taskId, TaskRequest request) {
        String userId = jwtService.isValid(token) ? jwtService.getUserId(token) : null;
        if (userId == null) {
            return null;
        }

        Optional<Task> taskOpt = taskOutputGateway.findById(taskId);
        if (taskOpt.isEmpty()) {
            return null;
        }

        Task task = taskOpt.get();
        TaskGroupResponse group = taskGroupOutputGateway.findById(task.getGroupId());
        if (group == null || !userId.equals(group.getUser_id())) {
            return null;
        }

        if (request.getGroup_id() != null && !request.getGroup_id().equals(task.getGroupId())) {
            TaskGroupResponse newGroup = taskGroupOutputGateway.findById(request.getGroup_id());
            if (newGroup == null || !userId.equals(newGroup.getUser_id())) {
                return null;
            }
            task.setGroupId(request.getGroup_id());
        }

        if (request.getName() != null) {
            task.setName(request.getName());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            try {
                task.setStatus(TaskStatusEnum.valueOf(request.getStatus()));
            } catch (IllegalArgumentException e) {
            }
        }

        Task updatedTask = taskOutputGateway.save(task);
        return toResponse(updatedTask);
    }

    @Override
    public void delete(String token, String taskId) {
        String userId = getUserIdFromToken(token);
        if (userId == null) {
            return;
        }

        Optional<Task> taskOpt = taskOutputGateway.findById(taskId);
        if (taskOpt.isEmpty()) {
            return;
        }

        Task task = taskOpt.get();
        TaskGroupResponse group = taskGroupOutputGateway.findById(task.getGroupId());
        if (group == null || !userId.equals(group.getUser_id())) {
            return;
        }

        taskOutputGateway.deleteById(taskId);
    }

    @Override
    public TaskResponse findById(String token, String taskId) {
        String userId = getUserIdFromToken(token);
        if (userId == null) {
            return null;
        }

        Optional<Task> taskOpt = taskOutputGateway.findById(taskId);
        if (taskOpt.isEmpty()) {
            return null;
        }

        Task task = taskOpt.get();
        TaskGroupResponse group = taskGroupOutputGateway.findById(task.getGroupId());
        if (group == null || !userId.equals(group.getUser_id())) {
            return null;
        }

        return toResponse(task);
    }

    @Override
    public List<TaskResponse> findAllByGroup(String token, String groupId) {
        String userId = getUserIdFromToken(token);
        if (userId == null) {
            return List.of();
        }

        TaskGroupResponse group = taskGroupOutputGateway.findById(groupId);
        if (group == null || !userId.equals(group.getUser_id())) {
            return List.of();
        }

        List<Task> tasks = taskOutputGateway.findAllByGroupId(groupId);
        return tasks.stream().map(this::toResponse).toList();
    }

    private String getUserIdFromToken(String token) {
        if (token == null) {
            return null;
        }
        return jwtService.isValid(token) ? jwtService.getUserId(token) : null;
    }

    private TaskResponse toResponse(Task task) {
        if (task == null) {
            return null;
        }
        return TaskResponse.builder()
                .id(task.getId())
                .group_id(task.getGroupId())
                .name(task.getName())
                .description(task.getDescription())
                .status(task.getStatus() != null ? task.getStatus().name() : null)
                .created_at(task.getCreatedAt())
                .build();
    }
}
