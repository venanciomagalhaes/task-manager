package br.com.taskmanager.presentation.viewmodel;

import br.com.taskmanager.application.dtos.CreateTaskDto;
import br.com.taskmanager.application.dtos.UpdateTaskDto;
import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.domain.enums.TaskStatus;
import br.com.taskmanager.domain.exceptions.ValidationException;
import br.com.taskmanager.presentation.util.InstantConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskFormViewModelTest {

    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");
    private static final Instant CREATED_AT = Instant.parse("2026-01-01T10:00:00Z");
    private static final Instant DEADLINE = Instant.parse("2026-01-02T13:30:00Z");

    private TaskFormViewModel viewModel;

    @BeforeEach
    void setUp() {
        viewModel = new TaskFormViewModel(new InstantConverter(ZONE));
    }

    @Test
    @DisplayName("monta CreateTaskDto com textos normalizados")
    void montaCreateDto() {
        viewModel.setName("  Estudar Java  ");
        viewModel.setDescription("   ");
        viewModel.setDeadlineDate(LocalDate.of(2026, 1, 2));
        viewModel.setDeadlineTime("10:30");

        CreateTaskDto dto = viewModel.toCreateDto();

        assertEquals("Estudar Java", dto.name());
        assertNull(dto.description());
        assertEquals(DEADLINE, dto.deadline());
    }

    @Test
    @DisplayName("carrega os dados da tarefa para edição")
    void carregaTask() {
        Task task = new Task(
                UUID.randomUUID(), "Tarefa", "Descrição", TaskStatus.IN_PROGRESS,
                DEADLINE, null, CREATED_AT, CREATED_AT, null
        );

        viewModel.load(task);

        assertEquals("Tarefa", viewModel.getName());
        assertEquals("Descrição", viewModel.getDescription());
        assertEquals(LocalDate.of(2026, 1, 2), viewModel.getDeadlineDate());
        assertEquals("10:30", viewModel.getDeadlineTime());
        assertEquals(TaskStatus.IN_PROGRESS, viewModel.getStatus());
    }

    @Test
    @DisplayName("monta UpdateTaskDto com o status selecionado")
    void montaUpdateDto() {
        viewModel.setName("Tarefa");
        viewModel.setDescription("Descrição");
        viewModel.setDeadlineDate(LocalDate.of(2026, 1, 2));
        viewModel.setDeadlineTime("10:30");
        viewModel.setStatus(TaskStatus.COMPLETED);

        UpdateTaskDto dto = viewModel.toUpdateDto();

        assertEquals("Tarefa", dto.name());
        assertEquals("Descrição", dto.description());
        assertEquals(TaskStatus.COMPLETED, dto.status());
        assertEquals(DEADLINE, dto.deadline());
    }

    @Test
    @DisplayName("rejeita nome em branco")
    void rejeitaNomeEmBranco() {
        viewModel.setName("   ");

        ValidationException exception = assertThrows(ValidationException.class, viewModel::toCreateDto);

        assertEquals("Informe o nome da tarefa", exception.getMessage());
    }

    @Test
    @DisplayName("reset volta para os valores padrão")
    void resetVoltaAoPadrao() {
        viewModel.setName("Alterado");
        viewModel.setStatus(TaskStatus.COMPLETED);

        viewModel.reset();

        assertEquals("", viewModel.getName());
        assertEquals("", viewModel.getDescription());
        assertEquals("18:00", viewModel.getDeadlineTime());
        assertEquals(TaskStatus.PENDING, viewModel.getStatus());
        assertEquals(LocalDate.now().plusDays(1), viewModel.getDeadlineDate());
    }
}
