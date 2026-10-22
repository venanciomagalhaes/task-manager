package br.com.taskmanager.application.usecases;

import br.com.taskmanager.application.repositories.TaskRepository;
import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.domain.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @ParameterizedTest(name = "withTrashed={0}")
    @ValueSource(booleans = {true, false})
    @DisplayName("delega o filtro para o repository")
    void delegaFiltro(boolean withTrashed) {
        List<Task> tasks = List.of(TaskFixture.task(TaskStatus.PENDING));
        when(repository.getAll(withTrashed)).thenReturn(tasks);

        List<Task> result = useCase.handle(withTrashed);

        assertEquals(tasks, result);
        verify(repository).getAll(withTrashed);
    }
}
