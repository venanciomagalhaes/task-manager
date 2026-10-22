package br.com.taskmanager.application.usecases;

import br.com.taskmanager.application.exceptions.TaskNotFoundException;
import br.com.taskmanager.application.repositories.TaskRepository;
import br.com.taskmanager.domain.entities.Task;

import java.util.Optional;
import java.util.UUID;

public class RestoreTaskUseCase {

    private final TaskRepository taskRepository;

    public RestoreTaskUseCase(TaskRepository repository){
        this.taskRepository = repository;
    }

    public Task handle(UUID uuid){
        Task task = this.taskRepository.findByUuid(uuid)
                .orElseThrow(() ->  new TaskNotFoundException(uuid));
        task.restore();
        return this.taskRepository.save(task);
    }
}
