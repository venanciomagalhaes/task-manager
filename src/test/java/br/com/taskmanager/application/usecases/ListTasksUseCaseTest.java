package br.com.taskmanager.application.usecases;

import br.com.taskmanager.application.queries.PageRequest;
import br.com.taskmanager.application.queries.PageResult;
import br.com.taskmanager.application.queries.TaskQuery;
import br.com.taskmanager.application.queries.TaskScope;
import br.com.taskmanager.application.repositories.TaskRepository;
import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.domain.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListTasksUseCaseTest {

    @Mock
    private TaskRepository repository;

    private ListTasksUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListTasksUseCase(repository);
    }

    @Test
    @DisplayName("delega consulta paginada para o repository")
    void delegaConsultaPaginada() {
        TaskQuery query = new TaskQuery("estudo", TaskStatus.PENDING, TaskScope.ACTIVE);
        PageRequest page = new PageRequest(1, 10);
        PageResult<Task> expected = new PageResult<>(
                List.of(TaskFixture.task(TaskStatus.PENDING)), 12, 1, 10);
        when(repository.search(query, page)).thenReturn(expected);

        PageResult<Task> result = useCase.handle(query, page);

        assertSame(expected, result);
        verify(repository).search(query, page);
    }
}
