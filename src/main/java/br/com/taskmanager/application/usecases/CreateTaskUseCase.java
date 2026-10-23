package br.com.taskmanager.application.usecases;

import br.com.taskmanager.application.dtos.CreateTaskDto;
import br.com.taskmanager.application.repositories.TaskRepository;
import br.com.taskmanager.domain.entities.Task;

public class CreateTaskUseCase {

    private final TaskRepository taskRepository;

    public CreateTaskUseCase(TaskRepository repository) {
        this.taskRepository = repository;
    }

    public Task handle(CreateTaskDto dto){
        return this.taskRepository.save(Task.create(
                dto.name(),
                dto.description(),
                dto.deadline()
        ));
    }

}
