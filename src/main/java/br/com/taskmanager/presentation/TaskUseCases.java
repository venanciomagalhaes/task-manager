package br.com.taskmanager.presentation;

import br.com.taskmanager.application.usecases.CreateTaskUseCase;
import br.com.taskmanager.application.usecases.DeleteTaskUseCase;
import br.com.taskmanager.application.usecases.ListTasksUseCase;
import br.com.taskmanager.application.usecases.RestoreTaskUseCase;
import br.com.taskmanager.application.usecases.ShowTaskUseCase;
import br.com.taskmanager.application.usecases.UpdateTaskUseCase;

public record TaskUseCases(
        CreateTaskUseCase create,
        UpdateTaskUseCase update,
        DeleteTaskUseCase delete,
        RestoreTaskUseCase restore,
        ShowTaskUseCase show,
        ListTasksUseCase list
) {
}
