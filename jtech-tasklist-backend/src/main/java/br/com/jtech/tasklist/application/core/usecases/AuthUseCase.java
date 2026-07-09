package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.adapters.input.protocols.AuthResponse;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.input.AuthInputGateway;
import br.com.jtech.tasklist.application.ports.output.AuthOutputGateway;

import java.util.Optional;

public class AuthUseCase implements AuthInputGateway {
    private final AuthOutputGateway authOutputGateway;

    public AuthUseCase(AuthOutputGateway authOutputGateway) {
        this.authOutputGateway = authOutputGateway;
    }

    @Override
    public AuthResponse register(User user) {
        return authOutputGateway.register(user);
    }

    @Override
    public Optional<User> login(User user) {
        return authOutputGateway.login(user);
    }
}
