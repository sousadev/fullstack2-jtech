package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.adapters.input.protocols.TaskGroupRequest;
import br.com.jtech.tasklist.adapters.input.protocols.TaskGroupResponse;
import br.com.jtech.tasklist.application.core.domains.TaskGroup;
import br.com.jtech.tasklist.application.ports.output.TaskGroupOutputGateway;
import br.com.jtech.tasklist.config.infra.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskGroupUseCaseTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private TaskGroupOutputGateway gateway;

    @InjectMocks
    private TaskGroupUseCase taskGroupUseCase;

    @Test
    @DisplayName("Should create task group successfully when token is valid")
    void shouldCreateTaskGroupSuccessfully() {
        // Arrange
        String token = "valid-token";
        String userId = "user-123";
        TaskGroupRequest request = TaskGroupRequest.builder()
                .name("Group Name")
                .description("Group Description")
                .build();

        TaskGroupResponse mockResponse = TaskGroupResponse.builder()
                .id("group-999")
                .name("Group Name")
                .description("Group Description")
                .user_id(userId)
                .build();

        when(jwtService.isValid(token)).thenReturn(true);
        when(jwtService.getUserId(token)).thenReturn(userId);
        when(gateway.create(eq(userId), any(TaskGroup.class))).thenReturn(mockResponse);

        // Act
        TaskGroupResponse response = taskGroupUseCase.create(token, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("group-999");
        assertThat(response.getUser_id()).isEqualTo(userId);

        verify(jwtService).isValid(token);
        verify(jwtService).getUserId(token);
        verify(gateway).create(eq(userId), any(TaskGroup.class));
    }

    @Test
    @DisplayName("Should return null for findById when token is invalid")
    void shouldReturnNullForFindByIdWhenTokenIsInvalid() {
        // Arrange
        String token = "invalid-token";
        String groupId = "group-123";

        when(jwtService.isValid(token)).thenReturn(false);

        // Act
        TaskGroupResponse response = taskGroupUseCase.findById(groupId, token);

        // Assert
        assertThat(response).isNull();
        verify(gateway, never()).findById(anyString());
    }

    @Test
    @DisplayName("Should return task group for findById when token is valid")
    void shouldReturnTaskGroupForFindByIdWhenTokenIsValid() {
        // Arrange
        String token = "valid-token";
        String groupId = "group-123";
        String userId = "user-123";
        TaskGroupResponse mockResponse = TaskGroupResponse.builder()
                .id(groupId)
                .name("Group Name")
                .user_id(userId)
                .build();

        when(jwtService.isValid(token)).thenReturn(true);
        when(jwtService.getUserId(token)).thenReturn(userId);
        when(gateway.findById(groupId)).thenReturn(mockResponse);

        // Act
        TaskGroupResponse response = taskGroupUseCase.findById(groupId, token);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(groupId);
        verify(gateway).findById(groupId);
    }

    @Test
    @DisplayName("Should return empty list for findAllByUser when token is invalid")
    void shouldReturnEmptyListForFindAllByUserWhenTokenIsInvalid() {
        // Arrange
        String token = "invalid-token";
        when(jwtService.isValid(token)).thenReturn(false);

        // Act
        List<TaskGroupResponse> response = taskGroupUseCase.findAllByUser(token);

        // Assert
        assertThat(response).isEmpty();
        verify(gateway, never()).findAllByUser(anyString());
    }

    @Test
    @DisplayName("Should return group list for findAllByUser when token is valid")
    void shouldReturnGroupListForFindAllByUserWhenTokenIsValid() {
        // Arrange
        String token = "valid-token";
        String userId = "user-123";
        List<TaskGroupResponse> mockList = List.of(
                TaskGroupResponse.builder().id("group-1").name("G1").user_id(userId).build(),
                TaskGroupResponse.builder().id("group-2").name("G2").user_id(userId).build()
        );

        when(jwtService.isValid(token)).thenReturn(true);
        when(jwtService.getSubject(token)).thenReturn(userId);
        when(gateway.findAllByUser(userId)).thenReturn(mockList);

        // Act
        List<TaskGroupResponse> response = taskGroupUseCase.findAllByUser(token);

        // Assert
        assertThat(response).hasSize(2);
        assertThat(response.get(0).getId()).isEqualTo("group-1");
        verify(gateway).findAllByUser(userId);
    }

    @Test
    @DisplayName("Should update task group successfully when authorized")
    void shouldUpdateTaskGroupSuccessfully() {
        // Arrange
        String id = "group-123";
        String token = "valid-token";
        String userId = "user-123";
        TaskGroupRequest request = TaskGroupRequest.builder()
                .name("New Name")
                .description("New Desc")
                .build();

        TaskGroupResponse existingGroup = TaskGroupResponse.builder()
                .id(id)
                .user_id(userId)
                .build();

        when(jwtService.isValid(token)).thenReturn(true);
        when(jwtService.getUserId(token)).thenReturn(userId);
        when(gateway.findById(id)).thenReturn(existingGroup);
        when(gateway.update(eq(id), any(TaskGroup.class))).thenReturn(existingGroup);

        // Act
        TaskGroupResponse response = taskGroupUseCase.update(id, request, token);

        // Assert
        assertThat(response).isNotNull();
        verify(gateway).update(eq(id), any(TaskGroup.class));
    }

    @Test
    @DisplayName("Should return null on update when unauthorized or group not found")
    void shouldReturnNullOnUpdateWhenUnauthorized() {
        // Arrange
        String id = "group-123";
        String token = "valid-token";
        String userId = "user-123";
        TaskGroupRequest request = TaskGroupRequest.builder().build();

        when(jwtService.isValid(token)).thenReturn(true);
        when(jwtService.getUserId(token)).thenReturn(userId);
        when(gateway.findById(id)).thenReturn(null);

        // Act
        TaskGroupResponse response = taskGroupUseCase.update(id, request, token);

        // Assert
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("Should delete task group successfully when authorized")
    void shouldDeleteTaskGroupSuccessfully() {
        // Arrange
        String id = "group-123";
        String token = "valid-token";
        String userId = "user-123";

        TaskGroupResponse existingGroup = TaskGroupResponse.builder()
                .id(id)
                .user_id(userId)
                .build();

        when(jwtService.isValid(token)).thenReturn(true);
        when(jwtService.getUserId(token)).thenReturn(userId);
        when(gateway.findById(id)).thenReturn(existingGroup);
        when(gateway.delete(id)).thenReturn(existingGroup);

        // Act
        TaskGroupResponse response = taskGroupUseCase.delete(id, token);

        // Assert
        assertThat(response).isNotNull();
        verify(gateway).delete(id);
    }
}
