package br.com.taskmanager.presentation.viewmodel;

import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.domain.enums.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskListViewModelTest {

    private static final Instant CREATED_AT = Instant.parse("2026-01-01T10:00:00Z");
    private static final Instant DEADLINE = CREATED_AT.plus(1, ChronoUnit.DAYS);

    private final TaskListViewModel viewModel = new TaskListViewModel();

    @Test
    @DisplayName("filtra por nome ignorando maiúsculas e minúsculas")
    void filtraPorNome() {
        Task estudar = task("Estudar Java", TaskStatus.PENDING, DEADLINE, null);
        Task comprar = task("Comprar café", TaskStatus.PENDING, DEADLINE, null);
        viewModel.setTasks(List.of(estudar, comprar));
        viewModel.setSearch("java");

        assertEquals(List.of(estudar), viewModel.visibleTasks());
    }

    @Test
    @DisplayName("filtra por status")
    void filtraPorStatus() {
        Task pendente = task("Pendente", TaskStatus.PENDING, DEADLINE, null);
        Task concluida = task("Concluída", TaskStatus.COMPLETED, DEADLINE, null);
        viewModel.setTasks(List.of(pendente, concluida));
        viewModel.setStatusFilter(TaskStatus.COMPLETED);

        assertEquals(List.of(concluida), viewModel.visibleTasks());
    }

    @Test
    @DisplayName("ordena as tarefas visíveis por prazo")
    void ordenaPorPrazo() {
        Task ultima = task("Última", TaskStatus.PENDING, DEADLINE.plus(1, ChronoUnit.HOURS), null);
        Task primeira = task("Primeira", TaskStatus.PENDING, DEADLINE.minus(1, ChronoUnit.HOURS), null);
        viewModel.setTasks(List.of(ultima, primeira));

        assertEquals(List.of(primeira, ultima), viewModel.visibleTasks());
    }

    @Test
    @DisplayName("lixeira retorna apenas excluídas, da mais recente para a mais antiga")
    void lixeiraRetornaApenasExcluidas() {
        Task ativa = task("Ativa", TaskStatus.PENDING, DEADLINE, null);
        Task antiga = task("Antiga", TaskStatus.PENDING, DEADLINE, CREATED_AT.plus(1, ChronoUnit.HOURS));
        Task recente = task("Recente", TaskStatus.PENDING, DEADLINE, CREATED_AT.plus(2, ChronoUnit.HOURS));

        assertEquals(List.of(recente, antiga), TaskListViewModel.deletedOnly(List.of(ativa, antiga, recente)));
    }

    @Test
    @DisplayName("define as ações disponíveis conforme o status")
    void defineAcoesDisponiveis() {
        Task pendente = task("Pendente", TaskStatus.PENDING, DEADLINE, null);
        Task emAndamento = task("Em andamento", TaskStatus.IN_PROGRESS, DEADLINE, null);
        Task concluida = task("Concluída", TaskStatus.COMPLETED, DEADLINE, null);
        Task cancelada = task("Cancelada", TaskStatus.CANCELED, DEADLINE, null);

        assertTrue(TaskListViewModel.canEdit(pendente));
        assertTrue(TaskListViewModel.canStart(pendente));
        assertTrue(TaskListViewModel.canStart(cancelada));
        assertTrue(TaskListViewModel.canComplete(pendente));
        assertTrue(TaskListViewModel.canComplete(emAndamento));
        assertFalse(TaskListViewModel.canComplete(concluida));
        assertFalse(TaskListViewModel.canStart(emAndamento));
        assertTrue(TaskListViewModel.canDelete(pendente));
    }

    @Test
    @DisplayName("tarefa excluída não permite edição nem restauração indevida")
    void tarefaExcluida() {
        Task excluida = task("Excluída", TaskStatus.PENDING, DEADLINE, CREATED_AT.plus(2, ChronoUnit.HOURS));

        assertFalse(TaskListViewModel.canEdit(excluida));
        assertFalse(TaskListViewModel.canComplete(excluida));
        assertTrue(TaskListViewModel.canRestore(excluida));
        assertFalse(TaskListViewModel.canRestore(task("Ativa", TaskStatus.PENDING, DEADLINE, null)));
    }

    private Task task(String name, TaskStatus status, Instant deadline, Instant deletedAt) {
        Instant finishedAt = status == TaskStatus.COMPLETED ? CREATED_AT.plus(30, ChronoUnit.MINUTES) : null;
        return new Task(
                UUID.randomUUID(),
                name,
                "descrição",
                status,
                deadline,
                finishedAt,
                CREATED_AT,
                CREATED_AT,
                deletedAt
        );
    }
}
