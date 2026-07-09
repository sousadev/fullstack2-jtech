package br.com.jtech.tasklist.application.core.domains;

import br.com.jtech.tasklist.adapters.input.protocols.CreateUserRequest;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.jtech.tasklist.adapters.input.protocols.AuthRequest;

import javax.swing.text.html.Option;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;
    private String name;
    private String email;
    private String password;
    private LocalDateTime createdAt;

    public static User of(CreateUserRequest request) {
        return User.builder()
                .id(UUID.randomUUID())
                .name(request.name)
                .email(request.email)
                .password(request.password)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static User from(CreateUserRequest request) {
        return of(request);
    }

    public static User of(AuthRequest request) {
        return User.builder()
                .id(UUID.randomUUID())
                .name(request.getEmail())
                .email(request.getEmail())
                .password(request.getPassword())
                .createdAt(LocalDateTime.now())
                .build();
    }

}
