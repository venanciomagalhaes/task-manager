package br.com.taskmanager.application.usecases;

import br.com.taskmanager.application.dtos.CreateTaskDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTaskUseCaseTest {

    @Mock
    private TaskRepository repository;

    private CreateTaskUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateTaskUseCase(repository);
    }

    @Test
    @DisplayName("cria tarefa PENDING e persiste")
    void criaTarefaPending() {
        Instant deadline = Instant.now().plusSeconds(3600);
        when(repository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task task = useCase.handle(new CreateTaskDto("Estudar Java", "revisar coleções", deadline));

        assertNotNull(task.getUuid());
        assertEquals(TaskStatus.PENDING, task.getStatus());
        assertEquals("Estudar Java", task.getName());
        assertEquals("revisar coleções", task.getDescription());
        assertEquals(deadline, task.getDeadline());
        assertNull(task.getFinishedAt());
        assertNull(task.getDeletedAt());
        assertEquals(task.getCreatedAt(), task.getUpdatedAt());
        verify(repository).save(task);
    }

    @Test
    @DisplayName("propaga ValidationException quando o nome é nulo")
    void rejeitaNomeNulo() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                useCase.handle(new CreateTaskDto(null, "descrição", Instant.now().plusSeconds(3600))));

        assertTrue(ex.getMessage().contains("nome"));
        verify(repository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("propaga ValidationException quando o deadline é nulo")
    void rejeitaDeadlineNulo() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                useCase.handle(new CreateTaskDto("nome", "descrição", null)));

        assertTrue(ex.getMessage().contains("prazo"));
        verify(repository, never()).save(any(Task.class));
    }
}
