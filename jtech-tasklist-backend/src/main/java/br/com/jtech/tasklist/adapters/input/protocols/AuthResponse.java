package br.com.jtech.tasklist.adapters.input.protocols;

import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskGroupEntity;
import br.com.jtech.tasklist.application.core.domains.TaskGroup;
import lombok.Builder;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public class AuthResponse implements Serializable {
    private UUID id;
    private String email;
    private String name;
    private String password;
    private LocalDateTime createdAt;
    public Array groups;
}
