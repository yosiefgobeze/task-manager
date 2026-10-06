package com.yosiefgobeze.taskmanager.service;

import com.yosiefgobeze.taskmanager.dto.CreateTaskRequest;
import com.yosiefgobeze.taskmanager.dto.TaskResponse;
import com.yosiefgobeze.taskmanager.dto.UpdateTaskRequest;
import com.yosiefgobeze.taskmanager.entity.Task;
import com.yosiefgobeze.taskmanager.exception.TaskNotFoundException;
import com.yosiefgobeze.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponse createTask(CreateTaskRequest request) {

        Task task = new Task(
                request.title(),
                request.description()
        );

        Task savedTask = taskRepository.save(task);

        return TaskResponse.from(savedTask);
    }

    public List<TaskResponse> getAllTasks() {

        return taskRepository.findAll()
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    public TaskResponse getTaskById(Long id) {

        Task task = findTaskById(id);

        return TaskResponse.from(task);
    }

    public TaskResponse updateTask(
            Long id,
            UpdateTaskRequest request) {

        Task existingTask = findTaskById(id);

        existingTask.setTitle(request.title());
        existingTask.setDescription(request.description());

        if (request.completed() != null) {
            existingTask.setCompleted(request.completed());
        }

        Task updatedTask = taskRepository.save(existingTask);

        return TaskResponse.from(updatedTask);
    }

    public void deleteTask(Long id) {

        Task existingTask = findTaskById(id);

        taskRepository.delete(existingTask);
    }

    private Task findTaskById(Long id) {

        return taskRepository.findById(id)
                .orElseThrow(() ->
                        new TaskNotFoundException(id)
                );

    }
}