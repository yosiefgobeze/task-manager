package com.yosiefgobeze.taskmanager.dto;

public record UpdateTaskRequest(
        String title,
        String description,
        Boolean completed
) {
}