package br.com.taskmanager.application.usecases;

import br.com.taskmanager.application.dtos.UpdateTaskDto;
import br.com.taskmanager.application.exceptions.TaskNotFoundException;
import br.com.taskmanager.application.repositories.TaskRepository;
import br.com.taskmanager.domain.entities.Task;

import java.util.Optional;
import java.util.UUID;

public class UpdateTaskUseCase {

    private final TaskRepository taskRepository;

    public UpdateTaskUseCase(TaskRepository repository){
        this.taskRepository = repository;
    }

    public Task handle(UUID uuid, UpdateTaskDto dto){
        Optional<Task> optionalTask = this.taskRepository.findByUuid(uuid);
        if (optionalTask.isEmpty()){
            throw new TaskNotFoundException("Não foi possível encontrar a task " + uuid.toString());
        }
        Task task = optionalTask.get();
        task.update(dto.name(), dto.description(), dto.status(), dto.deadline());

        return this.taskRepository.save(task);
    }
}
