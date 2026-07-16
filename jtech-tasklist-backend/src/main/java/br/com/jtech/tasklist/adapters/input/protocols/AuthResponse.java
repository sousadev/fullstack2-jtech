package br.com.jtech.tasklist.adapters.input.protocols;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse implements Serializable {
    private UUID id;
    private String email;
    private String name;
    private String token;
    private LocalDateTime createdAt;
    private List<TaskGroupResponse> taskGroups;
}
