package br.com.taskmanager.application.usecases;

import br.com.taskmanager.application.exceptions.TaskNotFoundException;
import br.com.taskmanager.application.repositories.TaskRepository;
import br.com.taskmanager.domain.entities.Task;

import java.util.UUID;

public class DeleteTaskUseCase {

    private final TaskRepository taskRepository;

    public DeleteTaskUseCase(TaskRepository repository) {
        this.taskRepository = repository;
    }

    public void handle(UUID uuid){
        Task task = this.taskRepository.findByUuid(uuid)
                .orElseThrow(() -> new TaskNotFoundException(uuid));
        task.delete();
        this.taskRepository.save(task);
    }

}
