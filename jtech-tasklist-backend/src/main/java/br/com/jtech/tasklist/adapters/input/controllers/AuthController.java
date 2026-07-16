package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.adapters.input.protocols.AuthRequest;
import br.com.jtech.tasklist.adapters.input.protocols.AuthResponse;
import br.com.jtech.tasklist.adapters.input.protocols.CreateUserRequest;
import br.com.jtech.tasklist.application.ports.input.AuthInputGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static br.com.jtech.tasklist.application.core.domains.User.of;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthInputGateway gateway;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody CreateUserRequest request) {
        AuthResponse response = gateway.register(of(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = gateway.login(of(request));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(@RequestBody String userId) {
        String response = gateway.refreshToken(userId);
        return ResponseEntity.ok(response);
    }
}
