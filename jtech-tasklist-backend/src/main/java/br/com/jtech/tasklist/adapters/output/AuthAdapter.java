package br.com.jtech.tasklist.adapters.output;

import br.com.jtech.tasklist.adapters.input.protocols.AuthResponse;
import br.com.jtech.tasklist.adapters.output.repositories.AuthRepository;
import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskGroupEntity;
import br.com.jtech.tasklist.adapters.output.repositories.entities.UserEntity;
import br.com.jtech.tasklist.application.core.domains.TaskGroup;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.output.AuthOutputGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthAdapter implements AuthOutputGateway {
    private final AuthRepository repository;

    @Override
    public AuthResponse register(User user) {
        UserEntity entity = UserEntity.toEntity(user);
        UserEntity saved = repository.save(entity);

        return AuthResponse.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .createdAt(saved.getCreatedAt())
                .name(saved.getName())
                .build();
    }

    @Override
    public Optional<User> login(User user) {
        Optional<UserEntity> entity = repository.findByEmail(user.getEmail());
        return entity.map(UserEntity::toDomain);
    }
}
