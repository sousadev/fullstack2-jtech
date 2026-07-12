package br.com.jtech.tasklist.application.ports.input;

import java.util.Optional;

import br.com.jtech.tasklist.adapters.input.protocols.AuthResponse;
import br.com.jtech.tasklist.application.core.domains.User;

public interface AuthInputGateway {
    AuthResponse register(User user);

    AuthResponse login(User user);

    Optional<User> findByEmail(String email);

    String refreshToken(String userId);
}
