package com.yosiefgobeze.taskmanager.service;

import static org.junit.jupiter.api.Assertions.*;

import com.yosiefgobeze.taskmanager.dto.CreateTaskRequest;
import com.yosiefgobeze.taskmanager.dto.TaskResponse;
import com.yosiefgobeze.taskmanager.dto.UpdateTaskRequest;
import com.yosiefgobeze.taskmanager.entity.Task;
import com.yosiefgobeze.taskmanager.exception.TaskNotFoundException;
import com.yosiefgobeze.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task(
                "Learn Spring Boot",
                "Build the TaskManager application"
        );

        task.setId(1L);
    }

    @Test
    void createTask_shouldCreateAndReturnTask() {

        CreateTaskRequest request = new CreateTaskRequest(
                "Learn Spring Boot",
                "Build the TaskManager application"
        );

        when(taskRepository.save(any(Task.class)))
                .thenReturn(task);

        TaskResponse response = taskService.createTask(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Learn Spring Boot", response.title());
        assertEquals(
                "Build the TaskManager application",
                response.description()
        );
        assertFalse(response.completed());

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void getAllTasks_shouldReturnAllTasks() {

        Task secondTask = new Task(
                "Learn React",
                "Build the frontend"
        );

        secondTask.setId(2L);

        when(taskRepository.findAll())
                .thenReturn(List.of(task, secondTask));

        List<TaskResponse> responses = taskService.getAllTasks();

        assertEquals(2, responses.size());

        assertEquals(1L, responses.get(0).id());
        assertEquals("Learn Spring Boot", responses.get(0).title());

        assertEquals(2L, responses.get(1).id());
        assertEquals("Learn React", responses.get(1).title());

        verify(taskRepository).findAll();
    }

    @Test
    void getTaskById_shouldReturnTask() {

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        TaskResponse response = taskService.getTaskById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Learn Spring Boot", response.title());

        verify(taskRepository).findById(1L);
    }

    @Test
    void getTaskById_shouldThrowExceptionWhenTaskDoesNotExist() {

        when(taskRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.getTaskById(999L)
        );

        verify(taskRepository).findById(999L);
    }

    @Test
    void updateTask_shouldUpdateExistingTask() {

        UpdateTaskRequest request = new UpdateTaskRequest(
                "Learn Spring Boot deeply",
                "Build a complete TaskManager application",
                true
        );

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(any(Task.class)))
                .thenReturn(task);

        TaskResponse response =
                taskService.updateTask(1L, request);

        assertEquals(1L, response.id());
        assertEquals(
                "Learn Spring Boot deeply",
                response.title()
        );
        assertEquals(
                "Build a complete TaskManager application",
                response.description()
        );
        assertTrue(response.completed());

        verify(taskRepository).findById(1L);
        verify(taskRepository).save(task);
    }

    @Test
    void updateTask_shouldThrowExceptionWhenTaskDoesNotExist() {

        UpdateTaskRequest request = new UpdateTaskRequest(
                "Doesn't exist",
                "Testing update",
                true
        );

        when(taskRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateTask(999L, request)
        );

        verify(taskRepository).findById(999L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTask_shouldKeepCompletedValueWhenCompletedIsNull() {

        task.setCompleted(true);

        UpdateTaskRequest request = new UpdateTaskRequest(
                "Updated title",
                "Updated description",
                null
        );

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(any(Task.class)))
                .thenReturn(task);

        TaskResponse response =
                taskService.updateTask(1L, request);

        assertTrue(response.completed());

        verify(taskRepository).save(task);
    }

    @Test
    void deleteTask_shouldDeleteExistingTask() {

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        taskService.deleteTask(1L);

        verify(taskRepository).findById(1L);
        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTask_shouldThrowExceptionWhenTaskDoesNotExist() {

        when(taskRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.deleteTask(999L)
        );

        verify(taskRepository).findById(999L);
        verify(taskRepository, never()).delete(any(Task.class));
    }
}