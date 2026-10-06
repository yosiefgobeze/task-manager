package com.yosiefgobeze.taskmanager.dto;

public record CreateTaskRequest(
        String title,
        String description
) {
}