package br.com.taskmanager.presentation.viewmodel;

import br.com.taskmanager.application.queries.PageRequest;
import br.com.taskmanager.application.queries.PageResult;
import br.com.taskmanager.application.queries.TaskScope;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskListViewModelTest {

    private static final Instant CREATED_AT = Instant.parse("2026-01-01T10:00:00Z");
    private static final Instant DEADLINE = CREATED_AT.plus(1, ChronoUnit.DAYS);

    private final TaskListViewModel viewModel = new TaskListViewModel();

    @Test
    @DisplayName("começa na primeira página com tamanho padrão")
    void comecaNaPrimeiraPagina() {
        assertEquals(new PageRequest(0, 10), viewModel.request());
        assertFalse(viewModel.hasPrevious());
        assertFalse(viewModel.hasNext());
        assertEquals("Página 1 de 1", viewModel.pageLabel());
        assertEquals("0 tarefas", viewModel.totalLabel());
    }

    @Test
    @DisplayName("aplica o resultado e habilita a próxima página")
    void aplicaPrimeiraPagina() {
        viewModel.apply(new PageResult<>(List.of(task("Tarefa")), 25, 0, 10));

        assertTrue(viewModel.hasNext());
        assertFalse(viewModel.hasPrevious());
        assertEquals("Página 1 de 3", viewModel.pageLabel());
        assertEquals("25 tarefas", viewModel.totalLabel());
        assertEquals(new PageRequest(1, 10), viewModel.nextRequest());
    }

    @Test
    @DisplayName("navega para trás a partir da segunda página")
    void segundaPagina() {
        viewModel.apply(new PageResult<>(List.of(task("Tarefa")), 25, 1, 10));

        assertTrue(viewModel.hasPrevious());
        assertEquals(new PageRequest(0, 10), viewModel.previousRequest());
    }

    @Test
    @DisplayName("não habilita próxima na última página")
    void ultimaPagina() {
        viewModel.apply(new PageResult<>(List.of(task("Tarefa")), 21, 2, 10));

        assertFalse(viewModel.hasNext());
        assertTrue(viewModel.hasPrevious());
        assertEquals("Página 3 de 3", viewModel.pageLabel());
    }

    @Test
    @DisplayName("busca volta para a primeira página e atualiza a consulta")
    void buscaResetaPagina() {
        viewModel.apply(new PageResult<>(List.of(task("Tarefa")), 30, 2, 10));

        viewModel.setSearch("  estudo  ");

        assertEquals("estudo", viewModel.query().search());
        assertEquals(new PageRequest(0, 10), viewModel.request());
        assertFalse(viewModel.hasPrevious());
    }

    @Test
    @DisplayName("filtro de status volta para a primeira página")
    void statusResetaPagina() {
        viewModel.apply(new PageResult<>(List.of(task("Tarefa")), 30, 1, 10));

        viewModel.setStatus(TaskStatus.COMPLETED);

        assertEquals(TaskStatus.COMPLETED, viewModel.query().status());
        assertEquals(new PageRequest(0, 10), viewModel.request());
    }

    @Test
    @DisplayName("limpar filtros remove busca e status")
    void limparFiltros() {
        viewModel.setSearch("estudo");
        viewModel.setStatus(TaskStatus.PENDING);

        viewModel.clearFilters();

        assertEquals("", viewModel.query().search());
        assertNull(viewModel.query().status());
        assertEquals(TaskScope.ACTIVE, viewModel.query().scope());
    }

    @Test
    @DisplayName("usa o escopo da lixeira")
    void escopoLixeira() {
        TaskListViewModel trash = new TaskListViewModel(TaskScope.TRASHED);

        assertEquals(TaskScope.TRASHED, trash.query().scope());
    }

    @Test
    @DisplayName("singulariza o total de uma tarefa")
    void totalSingular() {
        viewModel.apply(new PageResult<>(List.of(task("Tarefa")), 1, 0, 10));

        assertEquals("1 tarefa", viewModel.totalLabel());
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

    private Task task(String name) {
        return task(name, TaskStatus.PENDING, DEADLINE, null);
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
