package com.yosiefgobeze.taskmanager.controller;

import com.yosiefgobeze.taskmanager.dto.TaskResponse;
import com.yosiefgobeze.taskmanager.exception.GlobalExceptionHandler;
import com.yosiefgobeze.taskmanager.exception.TaskNotFoundException;
import com.yosiefgobeze.taskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
@WithMockUser(username = "taskmanager", roles = "USER")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    private TaskResponse taskResponse() {
        return new TaskResponse(
                1L,
                "Learn Spring Boot",
                "Build the TaskManager application",
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void createTask_shouldReturn201() throws Exception {

        when(taskService.createTask(any()))
                .thenReturn(taskResponse());

        mockMvc.perform(post("/api/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "title": "Learn Spring Boot",
                              "description": "Build the TaskManager application"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Learn Spring Boot"))
                .andExpect(jsonPath("$.description")
                        .value("Build the TaskManager application"))
                .andExpect(jsonPath("$.completed").value(false));

        verify(taskService).createTask(any());
    }

    @Test
    void createTask_shouldReturn400WhenTitleIsBlank() throws Exception {

        mockMvc.perform(post("/api/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "title": "",
                              "description": "No title"
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("title: Title is required"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(taskService);
    }

    @Test
    void getAllTasks_shouldReturn200() throws Exception {

        TaskResponse firstTask = taskResponse();

        TaskResponse secondTask = new TaskResponse(
                2L,
                "Learn React",
                "Build the frontend",
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(taskService.getAllTasks())
                .thenReturn(List.of(firstTask, secondTask));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title")
                        .value("Learn Spring Boot"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title")
                        .value("Learn React"));

        verify(taskService).getAllTasks();
    }

    @Test
    void getTaskById_shouldReturn200() throws Exception {

        when(taskService.getTaskById(1L))
                .thenReturn(taskResponse());

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title")
                        .value("Learn Spring Boot"))
                .andExpect(jsonPath("$.completed").value(false));

        verify(taskService).getTaskById(1L);
    }

    @Test
    void getTaskById_shouldReturn404WhenTaskDoesNotExist()
            throws Exception {

        when(taskService.getTaskById(999L))
                .thenThrow(new TaskNotFoundException(999L));

        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Task not found with id: 999"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(taskService).getTaskById(999L);
    }

    @Test
    void updateTask_shouldReturn200() throws Exception {

        TaskResponse updatedTask = new TaskResponse(
                1L,
                "Learn Spring Boot",
                "Build the TaskManager application",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(taskService.updateTask(eq(1L), any()))
                .thenReturn(updatedTask);

        mockMvc.perform(put("/api/tasks/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "title": "Learn Spring Boot",
                              "description": "Build the TaskManager application",
                              "completed": true
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title")
                        .value("Learn Spring Boot"))
                .andExpect(jsonPath("$.completed").value(true));

        verify(taskService).updateTask(eq(1L), any());
    }

    @Test
    void updateTask_shouldReturn404WhenTaskDoesNotExist()
            throws Exception {

        when(taskService.updateTask(eq(999L), any()))
                .thenThrow(new TaskNotFoundException(999L));

        mockMvc.perform(put("/api/tasks/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "title": "Doesn't exist",
                              "description": "Testing error handling",
                              "completed": true
                            }
                            """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Task not found with id: 999"));

        verify(taskService).updateTask(eq(999L), any());
    }

    @Test
    void updateTask_shouldReturn400WhenTitleIsBlank()
            throws Exception {

        mockMvc.perform(put("/api/tasks/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "title": "",
                              "description": "Invalid update",
                              "completed": true
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("title: Title is required"));

        verifyNoInteractions(taskService);
    }

    @Test
    void deleteTask_shouldReturn204() throws Exception {

        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(1L);
    }

    @Test
    void deleteTask_shouldReturn404WhenTaskDoesNotExist()
            throws Exception {

        doThrow(new TaskNotFoundException(999L))
                .when(taskService)
                .deleteTask(999L);

        mockMvc.perform(delete("/api/tasks/999")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Task not found with id: 999"));

        verify(taskService).deleteTask(999L);
    }

}