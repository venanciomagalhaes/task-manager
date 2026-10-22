package br.com.taskmanager.application.dtos;

import br.com.taskmanager.domain.enums.TaskStatus;

import java.time.Instant;

public record UpdateTaskDto(
        String name,
        String description,
        TaskStatus status,
        Instant deadline
) {
}
