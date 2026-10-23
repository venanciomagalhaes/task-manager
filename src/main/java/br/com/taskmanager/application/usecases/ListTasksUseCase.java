package br.com.taskmanager.application.usecases;

import br.com.taskmanager.application.repositories.TaskRepository;
import br.com.taskmanager.domain.entities.Task;

import java.util.List;

public class ListTasksUseCase {

    private final TaskRepository taskRepository;

    public ListTasksUseCase(TaskRepository repository) {
        this.taskRepository = repository;
    }

    public List<Task> handle(boolean withTrashed){
        return this.taskRepository.getAll(withTrashed);
    }
}
