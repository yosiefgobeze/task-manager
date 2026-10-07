package com.yosiefgobeze.taskmanager.controller;

import com.yosiefgobeze.taskmanager.dto.LoginResponse;
import com.yosiefgobeze.taskmanager.exception.GlobalExceptionHandler;
import com.yosiefgobeze.taskmanager.service.AuthService;
import com.yosiefgobeze.taskmanager.security.JwtService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import com.yosiefgobeze.taskmanager.security.CustomUserDetailsService;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;


    @MockitoBean
    private JwtService jwtService;


    @Test
    void register_shouldReturn201() throws Exception {

        doNothing()
                .when(authService)
                .register(any());

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "username": "taskmanager",
                                      "password": "password123"
                                    }
                                    """)
                )
                .andExpect(status().isCreated());

        verify(authService)
                .register(any());
    }


    @Test
    void register_shouldReturn400WhenUsernameIsBlank()
            throws Exception {

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "username": "",
                                      "password": "password123"
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(
                        jsonPath("$.message")
                                .value("username: Username is required")
                )
                .andExpect(
                        jsonPath("$.timestamp")
                                .exists()
                );

        verifyNoInteractions(authService);
    }

    @Test
    void register_shouldReturn400WhenUsernameIsTooShort()
            throws Exception {

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "username": "ab",
                                      "password": "password123"
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "username: Username must be between 3 and 50 characters"
                                )
                );

        verifyNoInteractions(authService);
    }

    @Test
    void register_shouldReturn400WhenPasswordIsTooShort()
            throws Exception {

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "username": "taskmanager",
                                      "password": "123"
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "password: Password must be between 6 and 100 characters"
                                )
                );

        verifyNoInteractions(authService);
    }

    @Test
    void login_shouldReturn200WithToken() throws Exception {

        when(authService.login(any()))
                .thenReturn(
                        new LoginResponse(
                                "jwt-token",
                                "taskmanager"
                        )
                );

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "username": "taskmanager",
                                      "password": "password123"
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.token")
                                .value("jwt-token")
                )
                .andExpect(
                        jsonPath("$.username")
                                .value("taskmanager")
                );

        verify(authService)
                .login(any());
    }

    @Test
    void login_shouldReturn400WhenUsernameIsBlank()
            throws Exception {

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "username": "",
                                      "password": "password123"
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(
                        jsonPath("$.message")
                                .value("username: Username is required")
                );

        verifyNoInteractions(authService);
    }

    @Test
    void login_shouldReturn400WhenPasswordIsBlank()
            throws Exception {

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "username": "taskmanager",
                                      "password": ""
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(
                        jsonPath("$.message")
                                .value("password: Password is required")
                );

        verifyNoInteractions(authService);
    }

}