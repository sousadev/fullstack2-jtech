package br.com.jtech.tasklist.application.ports.output;

import br.com.jtech.tasklist.adapters.input.protocols.AuthResponse;
import br.com.jtech.tasklist.application.core.domains.User;

import java.util.Optional;

public interface AuthOutputGateway {
    AuthResponse register(User user);
    Optional<User> login(User user);
    Optional<User> findByEmail(String email);
}
