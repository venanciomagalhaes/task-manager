package br.com.taskmanager.application.usecases;

import br.com.taskmanager.application.exceptions.TaskNotFoundException;
import br.com.taskmanager.application.repositories.TaskRepository;
import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.domain.enums.TaskStatus;
import br.com.taskmanager.domain.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestoreTaskUseCaseTest {

    @Mock
    private TaskRepository repository;

    private RestoreTaskUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RestoreTaskUseCase(repository);
    }

    @Test
    @DisplayName("limpa deletedAt e persiste")
    void restauraTask() {
        Task task = TaskFixture.deletedTask();
        Instant deletedAtAntes = task.getDeletedAt();
        when(repository.findByUuid(TaskFixture.TASK_UUID)).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = useCase.handle(TaskFixture.TASK_UUID);

        assertNull(task.getDeletedAt());
        assertFalse(task.getUpdatedAt().isBefore(deletedAtAntes));
        assertSame(task, result);
        verify(repository).save(task);
    }

    @Test
    @DisplayName("lança TaskNotFoundException quando a tarefa não existe")
    void tarefaNaoEncontrada() {
        when(repository.findByUuid(TaskFixture.TASK_UUID)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> useCase.handle(TaskFixture.TASK_UUID));

        verify(repository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("rejeita restaurar tarefa que não está deletada")
    void rejeitaTaskNaoDeletada() {
        Task task = TaskFixture.task(TaskStatus.PENDING);
        when(repository.findByUuid(TaskFixture.TASK_UUID)).thenReturn(Optional.of(task));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> useCase.handle(TaskFixture.TASK_UUID));

        assertEquals("A tarefa não está deletada, portanto não há o que restaurar", ex.getMessage());
        verify(repository, never()).save(any(Task.class));
    }
}
