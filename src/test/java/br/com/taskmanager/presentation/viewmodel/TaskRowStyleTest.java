package br.com.taskmanager.presentation.viewmodel;

import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.domain.enums.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskRowStyleTest {

    private static final Instant CREATED_AT = Instant.parse("2026-01-01T10:00:00Z");
    private static final Instant NOW = Instant.parse("2026-06-01T12:00:00Z");

    @Test
    @DisplayName("tarefa concluída fica verde mesmo com prazo vencido")
    void concluidaFicaVerde() {
        Task task = task(TaskStatus.COMPLETED, NOW.minus(1, ChronoUnit.DAYS), null);

        assertEquals(TaskRowStyle.COMPLETED, TaskRowStyle.of(task, NOW));
    }

    @Test
    @DisplayName("tarefa ativa com prazo vencido fica vermelha")
    void atrasadaFicaVermelha() {
        Task pendente = task(TaskStatus.PENDING, NOW.minus(1, ChronoUnit.SECONDS), null);
        Task emAndamento = task(TaskStatus.IN_PROGRESS, NOW.minus(1, ChronoUnit.DAYS), null);

        assertEquals(TaskRowStyle.OVERDUE, TaskRowStyle.of(pendente, NOW));
        assertEquals(TaskRowStyle.OVERDUE, TaskRowStyle.of(emAndamento, NOW));
    }

    @Test
    @DisplayName("tarefa ativa dentro do prazo fica branca")
    void noPrazoFicaBranca() {
        Task task = task(TaskStatus.PENDING, NOW.plus(1, ChronoUnit.MINUTES), null);

        assertEquals(TaskRowStyle.NONE, TaskRowStyle.of(task, NOW));
    }

    @Test
    @DisplayName("prazo exatamente agora não é atraso")
    void prazoAgoraNaoAtrasa() {
        Task task = task(TaskStatus.PENDING, NOW, null);

        assertEquals(TaskRowStyle.NONE, TaskRowStyle.of(task, NOW));
    }

    @Test
    @DisplayName("tarefa cancelada não fica vermelha")
    void canceladaFicaBranca() {
        Task task = task(TaskStatus.CANCELED, NOW.minus(1, ChronoUnit.DAYS), null);

        assertEquals(TaskRowStyle.NONE, TaskRowStyle.of(task, NOW));
    }

    @Test
    @DisplayName("tarefa excluída não fica vermelha")
    void excluidaFicaBranca() {
        Task task = task(TaskStatus.PENDING, NOW.minus(1, ChronoUnit.DAYS), NOW.minus(1, ChronoUnit.HOURS));

        assertEquals(TaskRowStyle.NONE, TaskRowStyle.of(task, NOW));
    }

    @Test
    @DisplayName("tarefa nula não recebe estilo")
    void nulaSemEstilo() {
        assertEquals(TaskRowStyle.NONE, TaskRowStyle.of(null, NOW));
    }

    private Task task(TaskStatus status, Instant deadline, Instant deletedAt) {
        Instant finishedAt = status == TaskStatus.COMPLETED ? CREATED_AT.plus(30, ChronoUnit.MINUTES) : null;
        return new Task(
                UUID.randomUUID(),
                "Tarefa",
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
