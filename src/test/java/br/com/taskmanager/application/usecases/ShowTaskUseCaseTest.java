package br.com.taskmanager.application.usecases;

import br.com.taskmanager.application.exceptions.TaskNotFoundException;
import br.com.taskmanager.application.repositories.TaskRepository;
import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.domain.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShowTaskUseCaseTest {

    @Mock
    private TaskRepository repository;

    private ShowTaskUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ShowTaskUseCase(repository);
    }

    @Test
    @DisplayName("retorna a tarefa encontrada")
    void retornaTask() {
        Task task = TaskFixture.task(TaskStatus.PENDING);
        when(repository.findByUuid(TaskFixture.TASK_UUID)).thenReturn(Optional.of(task));

        assertSame(task, useCase.handle(TaskFixture.TASK_UUID));
        verify(repository).findByUuid(TaskFixture.TASK_UUID);
    }

    @Test
    @DisplayName("lança TaskNotFoundException quando a tarefa não existe")
    void tarefaNaoEncontrada() {
        when(repository.findByUuid(TaskFixture.TASK_UUID)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> useCase.handle(TaskFixture.TASK_UUID));
    }
}
