package br.com.taskmanager.application.usecases;

import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.domain.enums.TaskStatus;

import java.time.Instant;
import java.util.UUID;

final class TaskFixture {

    static final UUID TASK_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    static final Instant CREATED_AT = Instant.parse("2026-01-01T10:00:00Z");
    static final Instant UPDATED_AT = Instant.parse("2026-01-01T11:00:00Z");
    static final Instant DEADLINE = Instant.parse("2026-01-02T10:00:00Z");

    private TaskFixture() {
    }

    static Task task(TaskStatus status) {
        Instant finishedAt = status == TaskStatus.COMPLETED ? UPDATED_AT : null;
        return new Task(
                TASK_UUID,
                "nome original",
                "descrição original",
                status,
                DEADLINE,
                finishedAt,
                CREATED_AT,
                UPDATED_AT,
                null
        );
    }

    static Task deletedTask() {
        Task task = task(TaskStatus.PENDING);
        task.delete();
        return task;
    }
}
