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
        Optional<Task> optionalTask = this.taskRepository.findByUuid(uuid);
        if (optionalTask.isEmpty()){
            throw new TaskNotFoundException("Não foi possível encontrar a task " + uuid.toString());
        }
        return optionalTask.get();
    }
}
