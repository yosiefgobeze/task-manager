package com.yosiefgobeze.taskmanager.service;

import com.yosiefgobeze.taskmanager.dto.LoginRequest;
import com.yosiefgobeze.taskmanager.dto.LoginResponse;
import com.yosiefgobeze.taskmanager.dto.RegisterRequest;
import com.yosiefgobeze.taskmanager.entity.User;
import com.yosiefgobeze.taskmanager.repository.UserRepository;
import com.yosiefgobeze.taskmanager.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User(
                "taskmanager",
                "encoded-password"
        );

        user.setId(1L);
    }

    @Test
    void register_shouldCreateUser() {

        RegisterRequest request =
                new RegisterRequest(
                        "taskmanager",
                        "password123"
                );

        when(userRepository.existsByUsername("taskmanager"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        authService.register(request);

        verify(userRepository)
                .existsByUsername("taskmanager");

        verify(passwordEncoder)
                .encode("password123");

        verify(userRepository)
                .save(
                        argThat(savedUser ->
                                savedUser.getUsername()
                                        .equals("taskmanager")
                                        && savedUser.getPassword()
                                        .equals(
                                                "encoded-password"
                                        )
                        )
                );
    }

    @Test
    void register_shouldThrowExceptionWhenUsernameAlreadyExists() {

        RegisterRequest request =
                new RegisterRequest(
                        "taskmanager",
                        "password123"
                );

        when(userRepository.existsByUsername("taskmanager"))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.register(request)
                );

        assertEquals(
                "Username is already taken",
                exception.getMessage()
        );

        verify(userRepository)
                .existsByUsername("taskmanager");

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void login_shouldAuthenticateAndReturnJwt() {

        LoginRequest request =
                new LoginRequest(
                        "taskmanager",
                        "password123"
                );

        when(userRepository.findByUsername("taskmanager"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(any(UserDetails.class)))
                .thenReturn("jwt-token");

        LoginResponse response =
                authService.login(request);

        assertNotNull(response);

        assertEquals(
                "jwt-token",
                response.token()
        );

        assertEquals(
                "taskmanager",
                response.username()
        );

        verify(authenticationManager)
                .authenticate(
                        any(
                                UsernamePasswordAuthenticationToken.class
                        )
                );

        verify(userRepository)
                .findByUsername("taskmanager");

        verify(jwtService)
                .generateToken(any(UserDetails.class));
    }

    @Test
    void login_shouldUseCorrectUsernameAndPassword() {

        LoginRequest request =
                new LoginRequest(
                        "taskmanager",
                        "password123"
                );

        when(userRepository.findByUsername("taskmanager"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(any(UserDetails.class)))
                .thenReturn("jwt-token");

        authService.login(request);

        verify(authenticationManager)
                .authenticate(
                        argThat(authentication ->
                                authentication
                                        .getPrincipal()
                                        .equals("taskmanager")
                                        &&
                                        authentication
                                                .getCredentials()
                                                .equals(
                                                        "password123"
                                                )
                        )
                );
    }

    @Test
    void login_shouldReturnUserFromDatabase() {

        LoginRequest request =
                new LoginRequest(
                        "taskmanager",
                        "password123"
                );

        when(userRepository.findByUsername("taskmanager"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(any(UserDetails.class)))
                .thenReturn("jwt-token");

        LoginResponse response =
                authService.login(request);

        assertEquals(
                "taskmanager",
                response.username()
        );

        verify(userRepository)
                .findByUsername("taskmanager");
    }
}