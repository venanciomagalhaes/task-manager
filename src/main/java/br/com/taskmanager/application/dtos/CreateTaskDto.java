package br.com.taskmanager.application.dtos;

import java.time.Instant;

public record CreateTaskDto(
        String name,
        String description,
        Instant deadline
) {
}
