package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.adapters.input.protocols.TaskGroupResponse;
import br.com.jtech.tasklist.adapters.input.protocols.TaskRequest;
import br.com.jtech.tasklist.adapters.input.protocols.TaskResponse;
import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.domains.TaskStatusEnum;
import br.com.jtech.tasklist.application.ports.output.TaskGroupOutputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskOutputGateway;
import br.com.jtech.tasklist.config.infra.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskUseCaseTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private TaskOutputGateway taskOutputGateway;

    @Mock
    private TaskGroupOutputGateway taskGroupOutputGateway;

    @InjectMocks
    private TaskUseCase taskUseCase;

    @Test
    @DisplayName("Should create task successfully when token and request are valid")
    void shouldCreateTaskSuccessfully() {
        // Arrange
        String token = "valid-token";
        String userId = "user-123";
        TaskRequest request = TaskRequest.builder()
                .name("New Task")
                .description("Task description")
                .group_id("group-123")
                .status("PENDING")
                .build();

        Task mockTask = Task.of(request);
        mockTask.setId("task-999");

        when(jwtService.isValid(token)).thenReturn(true);
        when(jwtService.getUserId(token)).thenReturn(userId);
        when(taskOutputGateway.save(any(Task.class))).thenReturn(mockTask);

        // Act
        TaskResponse response = taskUseCase.create(token, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("task-999");
        assertThat(response.getName()).isEqualTo("New Task");
        assertThat(response.getGroup_id()).isEqualTo("group-123");

        verify(jwtService).isValid(token);
        verify(jwtService).getUserId(token);
        verify(taskOutputGateway).save(any(Task.class));
    }

    @Test
    @DisplayName("Should return null on create when token is invalid")
    void shouldReturnNullOnCreateWhenTokenIsInvalid() {
        // Arrange
        String token = "invalid-token";
        TaskRequest request = TaskRequest.builder()
                .name("New Task")
                .group_id("group-123")
                .build();

        when(jwtService.isValid(token)).thenReturn(false);

        // Act
        TaskResponse response = taskUseCase.create(token, request);

        // Assert
        assertThat(response).isNull();
        verify(taskOutputGateway, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Should update task successfully")
    void shouldUpdateTaskSuccessfully() {
        // Arrange
        String token = "valid-token";
        String taskId = "task-123";
        String userId = "user-123";
        String groupId = "group-123";

        TaskRequest request = TaskRequest.builder()
                .name("Updated Task Name")
                .description("Updated Description")
                .status("IN_PROGRESS")
                .build();

        Task existingTask = Task.builder()
                .id(taskId)
                .name("Old Name")
                .description("Old Description")
                .status(TaskStatusEnum.PENDING)
                .groupId(groupId)
                .build();

        TaskGroupResponse groupResponse = TaskGroupResponse.builder()
                .id(groupId)
                .user_id(userId)
                .build();

        when(jwtService.isValid(token)).thenReturn(true);
        when(jwtService.getUserId(token)).thenReturn(userId);
        when(taskOutputGateway.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskGroupOutputGateway.findById(groupId)).thenReturn(groupResponse);
        when(taskOutputGateway.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TaskResponse response = taskUseCase.update(token, taskId, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Updated Task Name");
        assertThat(response.getDescription()).isEqualTo("Updated Description");
        assertThat(response.getStatus()).isEqualTo("IN_PROGRESS");

        verify(taskOutputGateway).save(existingTask);
    }

    @Test
    @DisplayName("Should return null on update when task does not exist")
    void shouldReturnNullOnUpdateWhenTaskDoesNotExist() {
        // Arrange
        String token = "valid-token";
        String taskId = "nonexistent-task";
        String userId = "user-123";

        when(jwtService.isValid(token)).thenReturn(true);
        when(jwtService.getUserId(token)).thenReturn(userId);
        when(taskOutputGateway.findById(taskId)).thenReturn(Optional.empty());

        // Act
        TaskResponse response = taskUseCase.update(token, taskId, new TaskRequest());

        // Assert
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("Should delete task when authorized")
    void shouldDeleteTaskWhenAuthorized() {
        // Arrange
        String token = "valid-token";
        String taskId = "task-123";
        String userId = "user-123";
        String groupId = "group-123";

        Task existingTask = Task.builder()
                .id(taskId)
                .groupId(groupId)
                .build();

        TaskGroupResponse groupResponse = TaskGroupResponse.builder()
                .id(groupId)
                .user_id(userId)
                .build();

        when(jwtService.isValid(token)).thenReturn(true);
        when(jwtService.getUserId(token)).thenReturn(userId);
        when(taskOutputGateway.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskGroupOutputGateway.findById(groupId)).thenReturn(groupResponse);

        // Act
        taskUseCase.delete(token, taskId);

        // Assert
        verify(taskOutputGateway).deleteById(taskId);
    }

    @Test
    @DisplayName("Should find task by id when authorized")
    void shouldFindTaskByIdWhenAuthorized() {
        // Arrange
        String token = "valid-token";
        String taskId = "task-123";
        String userId = "user-123";
        String groupId = "group-123";

        Task existingTask = Task.builder()
                .id(taskId)
                .name("Task Name")
                .groupId(groupId)
                .status(TaskStatusEnum.PENDING)
                .build();

        TaskGroupResponse groupResponse = TaskGroupResponse.builder()
                .id(groupId)
                .user_id(userId)
                .build();

        when(jwtService.isValid(token)).thenReturn(true);
        when(jwtService.getUserId(token)).thenReturn(userId);
        when(taskOutputGateway.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskGroupOutputGateway.findById(groupId)).thenReturn(groupResponse);

        // Act
        TaskResponse response = taskUseCase.findById(token, taskId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(taskId);
        assertThat(response.getName()).isEqualTo("Task Name");
    }

    @Test
    @DisplayName("Should find all tasks by group when authorized")
    void shouldFindAllTasksByGroupWhenAuthorized() {
        // Arrange
        String token = "valid-token";
        String userId = "user-123";
        String groupId = "group-123";

        TaskGroupResponse groupResponse = TaskGroupResponse.builder()
                .id(groupId)
                .user_id(userId)
                .build();

        List<Task> mockTasks = List.of(
                Task.builder().id("task-1").name("T1").groupId(groupId).status(TaskStatusEnum.PENDING).build(),
                Task.builder().id("task-2").name("T2").groupId(groupId).status(TaskStatusEnum.COMPLETED).build()
        );

        when(jwtService.isValid(token)).thenReturn(true);
        when(jwtService.getUserId(token)).thenReturn(userId);
        when(taskGroupOutputGateway.findById(groupId)).thenReturn(groupResponse);
        when(taskOutputGateway.findAllByGroupId(groupId)).thenReturn(mockTasks);

        // Act
        List<TaskResponse> response = taskUseCase.findAllByGroup(token, groupId);

        // Assert
        assertThat(response).hasSize(2);
        assertThat(response.get(0).getId()).isEqualTo("task-1");
        assertThat(response.get(1).getStatus()).isEqualTo("COMPLETED");

        verify(taskOutputGateway).findAllByGroupId(groupId);
    }
}
