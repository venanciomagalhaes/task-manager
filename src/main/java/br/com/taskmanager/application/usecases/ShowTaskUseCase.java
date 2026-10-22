package br.com.taskmanager.application.usecases;

import br.com.taskmanager.application.exceptions.TaskNotFoundException;
import br.com.taskmanager.application.repositories.TaskRepository;
import br.com.taskmanager.domain.entities.Task;

import java.util.Optional;
import java.util.UUID;

public class ShowTaskUseCase {
    private final TaskRepository taskRepository;

    public ShowTaskUseCase(TaskRepository repository){
        this.taskRepository = repository;
    }

    public Task handle(UUID uuid){
        return this.taskRepository.findByUuid(uuid)
                .orElseThrow(() -> new TaskNotFoundException(uuid));
    }
}
