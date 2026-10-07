package com.yosiefgobeze.taskmanager.service;

import com.yosiefgobeze.taskmanager.dto.CreateTaskRequest;
import com.yosiefgobeze.taskmanager.dto.TaskResponse;
import com.yosiefgobeze.taskmanager.dto.UpdateTaskRequest;
import com.yosiefgobeze.taskmanager.entity.Task;
import com.yosiefgobeze.taskmanager.entity.User;
import com.yosiefgobeze.taskmanager.exception.TaskNotFoundException;
import com.yosiefgobeze.taskmanager.repository.TaskRepository;
import com.yosiefgobeze.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {

        user = new User("taskmanager", "encoded-password");
        user.setId(1L);

        task = new Task(
                "Learn Spring Boot",
                "Build the TaskManager application"
        );

        task.setId(1L);
        task.setUser(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "taskmanager",
                        null,
                        List.of()
                )
        );

        when(userRepository.findByUsername("taskmanager"))
                .thenReturn(Optional.of(user));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createTask_shouldCreateTaskForCurrentUser() {

        CreateTaskRequest request = new CreateTaskRequest(
                "Learn Spring Boot",
                "Build the TaskManager application"
        );

        when(taskRepository.save(any(Task.class)))
                .thenReturn(task);

        TaskResponse response =
                taskService.createTask(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Learn Spring Boot", response.title());
        assertEquals(
                "Build the TaskManager application",
                response.description()
        );
        assertFalse(response.completed());

        verify(userRepository)
                .findByUsername("taskmanager");

        verify(taskRepository)
                .save(any(Task.class));
    }

    @Test
    void createTask_shouldAssignCurrentUserToTask() {

        CreateTaskRequest request = new CreateTaskRequest(
                "New task",
                "New description"
        );

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        taskService.createTask(request);

        verify(taskRepository).save(
                argThat(savedTask ->
                        savedTask.getUser() == user
                                && savedTask.getTitle().equals("New task")
                                && savedTask.getDescription()
                                .equals("New description")
                )
        );
    }

    @Test
    void getAllTasks_shouldReturnOnlyCurrentUsersTasks() {

        Task secondTask = new Task(
                "Learn React",
                "Build the frontend"
        );

        secondTask.setId(2L);
        secondTask.setUser(user);

        when(taskRepository.findAllByUser(user))
                .thenReturn(List.of(task, secondTask));

        List<TaskResponse> responses =
                taskService.getAllTasks();

        assertEquals(2, responses.size());

        assertEquals(1L, responses.get(0).id());
        assertEquals(
                "Learn Spring Boot",
                responses.get(0).title()
        );

        assertEquals(2L, responses.get(1).id());
        assertEquals(
                "Learn React",
                responses.get(1).title()
        );

        verify(taskRepository)
                .findAllByUser(user);

        verify(taskRepository, never())
                .findAll();
    }

    @Test
    void getTaskById_shouldReturnCurrentUsersTask() {

        when(taskRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(task));

        TaskResponse response =
                taskService.getTaskById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(
                "Learn Spring Boot",
                response.title()
        );

        verify(taskRepository)
                .findByIdAndUser(1L, user);
    }

    @Test
    void getTaskById_shouldThrowExceptionWhenTaskDoesNotExist() {

        when(taskRepository.findByIdAndUser(999L, user))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.getTaskById(999L)
        );

        verify(taskRepository)
                .findByIdAndUser(999L, user);
    }

    @Test
    void getTaskById_shouldThrowExceptionWhenTaskBelongsToAnotherUser() {

        when(taskRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.getTaskById(1L)
        );

        verify(taskRepository)
                .findByIdAndUser(1L, user);
    }

    @Test
    void updateTask_shouldUpdateCurrentUsersTask() {

        UpdateTaskRequest request = new UpdateTaskRequest(
                "Learn Spring Boot deeply",
                "Build a complete TaskManager application",
                true
        );

        when(taskRepository.findByIdAndUser(1L, user))
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

        verify(taskRepository)
                .findByIdAndUser(1L, user);

        verify(taskRepository)
                .save(task);
    }

    @Test
    void updateTask_shouldThrowExceptionWhenTaskDoesNotExist() {

        UpdateTaskRequest request = new UpdateTaskRequest(
                "Doesn't exist",
                "Testing update",
                true
        );

        when(taskRepository.findByIdAndUser(999L, user))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateTask(999L, request)
        );

        verify(taskRepository)
                .findByIdAndUser(999L, user);

        verify(taskRepository, never())
                .save(any(Task.class));
    }

    @Test
    void updateTask_shouldThrowExceptionWhenTaskBelongsToAnotherUser() {

        UpdateTaskRequest request = new UpdateTaskRequest(
                "Trying to modify another user's task",
                "Should not work",
                true
        );

        when(taskRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateTask(1L, request)
        );

        verify(taskRepository)
                .findByIdAndUser(1L, user);

        verify(taskRepository, never())
                .save(any(Task.class));
    }

    @Test
    void updateTask_shouldKeepCompletedValueWhenCompletedIsNull() {

        task.setCompleted(true);

        UpdateTaskRequest request = new UpdateTaskRequest(
                "Updated title",
                "Updated description",
                null
        );

        when(taskRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(any(Task.class)))
                .thenReturn(task);

        TaskResponse response =
                taskService.updateTask(1L, request);

        assertTrue(response.completed());

        assertEquals(
                "Updated title",
                response.title()
        );

        assertEquals(
                "Updated description",
                response.description()
        );

        verify(taskRepository)
                .save(task);
    }

    @Test
    void updateTask_shouldSetCompletedToFalse() {

        task.setCompleted(true);

        UpdateTaskRequest request = new UpdateTaskRequest(
                "Updated title",
                "Updated description",
                false
        );

        when(taskRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(any(Task.class)))
                .thenReturn(task);

        TaskResponse response =
                taskService.updateTask(1L, request);

        assertFalse(response.completed());

        verify(taskRepository)
                .save(task);
    }

    @Test
    void deleteTask_shouldDeleteCurrentUsersTask() {

        when(taskRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(task));

        taskService.deleteTask(1L);

        verify(taskRepository)
                .findByIdAndUser(1L, user);

        verify(taskRepository)
                .delete(task);
    }

    @Test
    void deleteTask_shouldThrowExceptionWhenTaskDoesNotExist() {

        when(taskRepository.findByIdAndUser(999L, user))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.deleteTask(999L)
        );

        verify(taskRepository)
                .findByIdAndUser(999L, user);

        verify(taskRepository, never())
                .delete(any(Task.class));
    }

    @Test
    void deleteTask_shouldNotDeleteAnotherUsersTask() {

        when(taskRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.deleteTask(1L)
        );

        verify(taskRepository)
                .findByIdAndUser(1L, user);

        verify(taskRepository, never())
                .delete(any(Task.class));
    }

    @Test
    void serviceShouldThrowWhenAuthenticatedUserDoesNotExist() {

        when(userRepository.findByUsername("taskmanager"))
                .thenReturn(Optional.empty());

        CreateTaskRequest request = new CreateTaskRequest(
                "Test",
                "Test"
        );

        assertThrows(
                RuntimeException.class,
                () -> taskService.createTask(request)
        );

        verify(taskRepository, never())
                .save(any(Task.class));
    }
}