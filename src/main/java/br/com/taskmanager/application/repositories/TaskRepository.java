package br.com.taskmanager.application.repositories;

import br.com.taskmanager.application.queries.PageRequest;
import br.com.taskmanager.application.queries.PageResult;
import br.com.taskmanager.application.queries.TaskQuery;
import br.com.taskmanager.domain.entities.Task;

import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {
    Optional<Task> findByUuid(UUID uuid);
    PageResult<Task> search(TaskQuery query, PageRequest page);
    Task save(Task task);
}
