package com.yosiefgobeze.taskmanager.dto;

public record LoginResponse(
        String token,
        String username
) {
}
