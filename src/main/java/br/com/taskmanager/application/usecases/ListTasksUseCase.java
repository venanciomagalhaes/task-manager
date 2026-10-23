package br.com.taskmanager.application.usecases;

import br.com.taskmanager.application.queries.PageRequest;
import br.com.taskmanager.application.queries.PageResult;
import br.com.taskmanager.application.queries.TaskQuery;
import br.com.taskmanager.application.repositories.TaskRepository;
import br.com.taskmanager.domain.entities.Task;

public class ListTasksUseCase {

    private final TaskRepository taskRepository;

    public ListTasksUseCase(TaskRepository repository) {
        this.taskRepository = repository;
    }

    public PageResult<Task> handle(TaskQuery query, PageRequest page) {
        return this.taskRepository.search(query, page);
    }
}
