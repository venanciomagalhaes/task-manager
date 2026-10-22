package br.com.taskmanager.application.usecases;

import br.com.taskmanager.application.dtos.UpdateTaskDto;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateTaskUseCaseTest {

    @Mock
    private TaskRepository repository;

    private UpdateTaskUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateTaskUseCase(repository);
    }

    @Test
    @DisplayName("renomeia mantendo o status atual")
    void renomeiaMantendoStatus() {
        Task task = TaskFixture.task(TaskStatus.PENDING);
        Instant updatedAtAntes = task.getUpdatedAt();
        when(repository.findByUuid(TaskFixture.TASK_UUID)).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = useCase.handle(TaskFixture.TASK_UUID,
                new UpdateTaskDto("nome novo", "descrição original", TaskStatus.PENDING, TaskFixture.DEADLINE));

        assertEquals("nome novo", task.getName());
        assertEquals(TaskStatus.PENDING, task.getStatus());
        assertNull(task.getFinishedAt());
        assertFalse(task.getUpdatedAt().isBefore(updatedAtAntes));
        assertSame(task, result);
        verify(repository).save(task);
    }

    @Test
    @DisplayName("transição para COMPLETED preenche finishedAt igual a updatedAt")
    void transicionaParaCompleted() {
        Task task = TaskFixture.task(TaskStatus.PENDING);
        when(repository.findByUuid(TaskFixture.TASK_UUID)).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.handle(TaskFixture.TASK_UUID,
                new UpdateTaskDto("nome original", "descrição original", TaskStatus.COMPLETED, TaskFixture.DEADLINE));

        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertNotNull(task.getFinishedAt());
        assertEquals(task.getUpdatedAt(), task.getFinishedAt());
        verify(repository).save(task);
    }

    @Test
    @DisplayName("PUT em tarefa COMPLETED preserva finishedAt")
    void preservaFinishedAtEmTaskCompleted() {
        Task task = TaskFixture.task(TaskStatus.COMPLETED);
        Instant finishedAtAntes = task.getFinishedAt();
        when(repository.findByUuid(TaskFixture.TASK_UUID)).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.handle(TaskFixture.TASK_UUID,
                new UpdateTaskDto("renomeada", "descrição original", TaskStatus.COMPLETED, TaskFixture.DEADLINE));

        assertEquals("renomeada", task.getName());
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertEquals(finishedAtAntes, task.getFinishedAt());
        verify(repository).save(task);
    }

    @Test
    @DisplayName("lança TaskNotFoundException quando a tarefa não existe")
    void tarefaNaoEncontrada() {
        when(repository.findByUuid(TaskFixture.TASK_UUID)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> useCase.handle(TaskFixture.TASK_UUID,
                new UpdateTaskDto("nome", "descrição", TaskStatus.PENDING, TaskFixture.DEADLINE)));

        verify(repository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("bloqueia update de tarefa deletada")
    void bloqueiaTarefaDeletada() {
        Task task = TaskFixture.deletedTask();
        when(repository.findByUuid(TaskFixture.TASK_UUID)).thenReturn(Optional.of(task));

        assertThrows(ValidationException.class, () -> useCase.handle(TaskFixture.TASK_UUID,
                new UpdateTaskDto("nome", "descrição", TaskStatus.PENDING, TaskFixture.DEADLINE)));

        verify(repository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("rejeita status nulo")
    void rejeitaStatusNulo() {
        Task task = TaskFixture.task(TaskStatus.PENDING);
        when(repository.findByUuid(TaskFixture.TASK_UUID)).thenReturn(Optional.of(task));

        ValidationException ex = assertThrows(ValidationException.class, () -> useCase.handle(TaskFixture.TASK_UUID,
                new UpdateTaskDto("nome", "descrição", null, TaskFixture.DEADLINE)));

        assertEquals("O novo status é obrigatório", ex.getMessage());
        verify(repository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("rejeita deadline anterior a createdAt")
    void rejeitaDeadlineAnteriorACreatedAt() {
        Task task = TaskFixture.task(TaskStatus.PENDING);
        when(repository.findByUuid(TaskFixture.TASK_UUID)).thenReturn(Optional.of(task));

        assertThrows(ValidationException.class, () -> useCase.handle(TaskFixture.TASK_UUID,
                new UpdateTaskDto("nome", "descrição", TaskStatus.PENDING, TaskFixture.CREATED_AT.minusSeconds(1))));

        verify(repository, never()).save(any(Task.class));
    }
}
