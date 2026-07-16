package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.adapters.input.protocols.AuthResponse;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.output.AuthOutputGateway;
import br.com.jtech.tasklist.config.infra.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private AuthOutputGateway authOutputGateway;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthUseCase authUseCase;

    @Test
    @DisplayName("Should register user successfully when email is not registered")
    void shouldRegisterUserSuccessfully() {
        // Arrange
        User user = User.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("password123")
                .build();

        UUID generatedId = UUID.randomUUID();
        AuthResponse mockResponse = AuthResponse.builder()
                .id(generatedId)
                .name("John Doe")
                .email("john@example.com")
                .build();

        when(authOutputGateway.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(authOutputGateway.register(user)).thenReturn(mockResponse);
        when(jwtService.generateToken(generatedId.toString())).thenReturn("mockedToken");

        // Act
        AuthResponse response = authUseCase.register(user);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mockedToken");
        assertThat(response.getEmail()).isEqualTo("john@example.com");

        verify(authOutputGateway).findByEmail("john@example.com");
        verify(passwordEncoder).encode("password123");
        verify(authOutputGateway).register(user);
        verify(jwtService).generateToken(generatedId.toString());
    }

    @Test
    @DisplayName("Should return error message when registering with an existing email")
    void shouldReturnErrorWhenEmailAlreadyExists() {
        // Arrange
        User user = User.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("password123")
                .build();

        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .email("john@example.com")
                .build();

        when(authOutputGateway.findByEmail(user.getEmail())).thenReturn(Optional.of(existingUser));

        // Act
        AuthResponse response = authUseCase.register(user);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("Email já cadastrado!");
        assertThat(response.getToken()).isNull();

        verify(authOutputGateway).findByEmail("john@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(authOutputGateway, never()).register(any(User.class));
    }

    @Test
    @DisplayName("Should login successfully when credentials are correct")
    void shouldLoginSuccessfully() {
        // Arrange
        User loginRequest = User.builder()
                .email("john@example.com")
                .password("password123")
                .build();

        UUID existingId = UUID.randomUUID();
        User existingUser = User.builder()
                .id(existingId)
                .name("John Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .build();

        when(authOutputGateway.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), existingUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken(existingId.toString())).thenReturn("mockedToken");

        // Act
        AuthResponse response = authUseCase.login(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mockedToken");
        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getEmail()).isEqualTo("john@example.com");

        verify(authOutputGateway).findByEmail("john@example.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtService).generateToken(existingId.toString());
    }

    @Test
    @DisplayName("Should return error message when login password does not match")
    void shouldReturnErrorWhenLoginPasswordIsWrong() {
        // Arrange
        User loginRequest = User.builder()
                .email("john@example.com")
                .password("wrongPassword")
                .build();

        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .email("john@example.com")
                .password("encodedPassword")
                .build();

        when(authOutputGateway.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), existingUser.getPassword())).thenReturn(false);

        // Act
        AuthResponse response = authUseCase.login(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("Email ou senha inválidos!");
        assertThat(response.getToken()).isNull();

        verify(authOutputGateway).findByEmail("john@example.com");
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("Should return error message when login email is not found")
    void shouldReturnErrorWhenLoginEmailNotFound() {
        // Arrange
        User loginRequest = User.builder()
                .email("nonexistent@example.com")
                .password("password123")
                .build();

        when(authOutputGateway.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        // Act
        AuthResponse response = authUseCase.login(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("Email ou senha inválidos!");
        assertThat(response.getToken()).isNull();

        verify(authOutputGateway).findByEmail("nonexistent@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // Arrange
        String email = "john@example.com";
        User user = User.builder().email(email).build();
        when(authOutputGateway.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = authUseCase.findByEmail(email);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void shouldRefreshTokenSuccessfully() {
        // Arrange
        String userId = "user-123";
        when(jwtService.refreshToken(userId)).thenReturn("refreshedToken");

        // Act
        String token = authUseCase.refreshToken(userId);

        // Assert
        assertThat(token).isEqualTo("refreshedToken");
        verify(jwtService).refreshToken(userId);
    }
}
