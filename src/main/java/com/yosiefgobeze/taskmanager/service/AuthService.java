package com.yosiefgobeze.taskmanager.service;

import com.yosiefgobeze.taskmanager.dto.LoginRequest;
import com.yosiefgobeze.taskmanager.dto.LoginResponse;
import com.yosiefgobeze.taskmanager.dto.RegisterRequest;
import com.yosiefgobeze.taskmanager.entity.User;
import com.yosiefgobeze.taskmanager.repository.UserRepository;
import com.yosiefgobeze.taskmanager.security.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequest request) {

        if (userRepository.existsByUsername(
                request.username())) {

            throw new IllegalArgumentException(
                    "Username is already taken"
            );
        }

        User user = new User(
                request.username(),
                passwordEncoder.encode(request.password())
        );

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserDetails userDetails =
                userRepository
                        .findByUsername(request.username())
                        .map(user ->
                                org.springframework.security.core.userdetails.User
                                        .withUsername(user.getUsername())
                                        .password(user.getPassword())
                                        .roles("USER")
                                        .build()
                        )
                        .orElseThrow();

        String token =
                jwtService.generateToken(userDetails);

        return new LoginResponse(
                token,
                userDetails.getUsername()
        );
    }
}
