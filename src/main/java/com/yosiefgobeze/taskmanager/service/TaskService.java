package com.yosiefgobeze.taskmanager.service;

import com.yosiefgobeze.taskmanager.dto.CreateTaskRequest;
import com.yosiefgobeze.taskmanager.dto.TaskResponse;
import com.yosiefgobeze.taskmanager.dto.UpdateTaskRequest;
import com.yosiefgobeze.taskmanager.entity.Task;
import com.yosiefgobeze.taskmanager.entity.User;
import com.yosiefgobeze.taskmanager.exception.TaskNotFoundException;
import com.yosiefgobeze.taskmanager.repository.TaskRepository;
import com.yosiefgobeze.taskmanager.repository.UserRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(
            TaskRepository taskRepository,
            UserRepository userRepository) {

        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public TaskResponse createTask(
            CreateTaskRequest request) {

        User user = getCurrentUser();

        Task task = new Task(
                request.title(),
                request.description()
        );

        task.setUser(user);

        Task savedTask =
                taskRepository.save(task);

        return TaskResponse.from(savedTask);
    }

    public List<TaskResponse> getAllTasks() {

        User user = getCurrentUser();

        return taskRepository
                .findAllByUser(user)
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    public TaskResponse getTaskById(Long id) {

        User user = getCurrentUser();

        Task task =
                taskRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new TaskNotFoundException(id)
                        );

        return TaskResponse.from(task);
    }

    public TaskResponse updateTask(
            Long id,
            UpdateTaskRequest request) {

        User user = getCurrentUser();

        Task existingTask =
                taskRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new TaskNotFoundException(id)
                        );

        existingTask.setTitle(request.title());
        existingTask.setDescription(
                request.description()
        );

        if (request.completed() != null) {
            existingTask.setCompleted(
                    request.completed()
            );
        }

        Task updatedTask =
                taskRepository.save(existingTask);

        return TaskResponse.from(updatedTask);
    }

    public void deleteTask(Long id) {

        User user = getCurrentUser();

        Task existingTask =
                taskRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new TaskNotFoundException(id)
                        );

        taskRepository.delete(existingTask);
    }

    private User getCurrentUser() {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return userRepository
                .findByUsername(username)
                .orElseThrow();
    }
}