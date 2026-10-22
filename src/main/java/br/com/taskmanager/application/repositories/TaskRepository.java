package br.com.taskmanager.application.repositories;

import br.com.taskmanager.application.dtos.CreateTaskDto;
import br.com.taskmanager.application.dtos.UpdateTaskDto;
import br.com.taskmanager.domain.entities.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {
    Optional<Task> findByUuid(UUID uuid);
    List<Task> getAll(boolean withTrashed);
    Task save(Task task);
}
