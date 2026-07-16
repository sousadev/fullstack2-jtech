package br.com.jtech.tasklist.application.core.usecases;

import java.util.Optional;

import br.com.jtech.tasklist.adapters.input.protocols.AuthResponse;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.input.AuthInputGateway;
import br.com.jtech.tasklist.application.ports.output.AuthOutputGateway;
import br.com.jtech.tasklist.config.infra.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthUseCase implements AuthInputGateway {
    private final AuthOutputGateway authOutputGateway;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthUseCase(AuthOutputGateway authOutputGateway, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.authOutputGateway = authOutputGateway;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse register(User user) {
        Optional<User> userOptional = authOutputGateway.findByEmail(user.getEmail());
        if (userOptional.isPresent()) {
            return AuthResponse.builder()
                    .email("Email já cadastrado!")
                    .build();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        AuthResponse response = authOutputGateway.register(user);
        response.setToken(jwtService.generateToken(response.getId().toString()));
        return response;
    }

    @Override
    public AuthResponse login(User user) {
        Optional<User> userOptional = authOutputGateway.findByEmail(user.getEmail());
        if (userOptional.isPresent() && passwordEncoder.matches(user.getPassword(), userOptional.get().getPassword())) {
            User existingUser = userOptional.get();
            String token = jwtService.generateToken(existingUser.getId().toString());
            return AuthResponse.builder()
                    .id(existingUser.getId())
                    .email(existingUser.getEmail())
                    .createdAt(existingUser.getCreatedAt())
                    .name(existingUser.getName())
                    .token(token)
                    .build();
        }
        return AuthResponse.builder()
                .email("Email ou senha inválidos!")
                .build();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<User> user = authOutputGateway.findByEmail(email);
        return user;
    }

    @Override
    public String refreshToken(String userId) {
        String token = jwtService.refreshToken(userId);
        return token;
    }
}
