package br.com.taskmanager.application.usecases;

import br.com.taskmanager.application.exceptions.TaskNotFoundException;
import br.com.taskmanager.application.repositories.TaskRepository;
import br.com.taskmanager.domain.entities.Task;

import java.util.Optional;
import java.util.UUID;

public class DeleteTaskUseCase {

    private final TaskRepository taskRepository;

    public DeleteTaskUseCase(TaskRepository repository){
        this.taskRepository = repository;
    }

    public void handle(UUID uuid){
        Optional<Task> optionalTask = this.taskRepository.findByUuid(uuid);
        if (optionalTask.isEmpty()){
            throw new TaskNotFoundException("Não foi possível encontrar a task " + uuid.toString());
        }
        Task task = optionalTask.get();
        task.delete();
        this.taskRepository.save(task);
    }

}
