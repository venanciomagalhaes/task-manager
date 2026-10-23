package br.com.taskmanager.presentation.viewmodel;

import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.domain.enums.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CompletionTimingTest {

    private static final Instant CREATED_AT = Instant.parse("2026-01-01T10:00:00Z");
    private static final Instant DEADLINE = CREATED_AT.plus(1, ChronoUnit.DAYS);

    @Test
    @DisplayName("finalizada antes do prazo é No prazo")
    void antesDoPrazo() {
        Task task = completedTask(DEADLINE.minus(1, ChronoUnit.HOURS));

        assertEquals(CompletionTiming.ON_TIME, CompletionTiming.of(task));
    }

    @Test
    @DisplayName("finalizada exatamente no prazo é No prazo")
    void noPrazo() {
        Task task = completedTask(DEADLINE);

        assertEquals(CompletionTiming.ON_TIME, CompletionTiming.of(task));
    }

    @Test
    @DisplayName("finalizada depois do prazo é Atrasada")
    void depoisDoPrazo() {
        Task task = completedTask(DEADLINE.plus(1, ChronoUnit.SECONDS));

        assertEquals(CompletionTiming.LATE, CompletionTiming.of(task));
    }

    @Test
    @DisplayName("tarefa não finalizada não tem badge")
    void naoFinalizada() {
        Task task = new Task(
                UUID.randomUUID(), "Tarefa", "descrição", TaskStatus.PENDING,
                DEADLINE, null, CREATED_AT, CREATED_AT, null
        );

        assertNull(CompletionTiming.of(task));
        assertNull(CompletionTiming.of(null));
    }

    @Test
    @DisplayName("rótulos e classes de estilo")
    void rotulosEClasses() {
        assertEquals("No prazo", CompletionTiming.ON_TIME.label());
        assertEquals("timing-on-time", CompletionTiming.ON_TIME.styleClass());
        assertEquals("Atrasada", CompletionTiming.LATE.label());
        assertEquals("timing-late", CompletionTiming.LATE.styleClass());
    }

    private Task completedTask(Instant finishedAt) {
        return new Task(
                UUID.randomUUID(), "Tarefa", "descrição", TaskStatus.COMPLETED,
                DEADLINE, finishedAt, CREATED_AT, finishedAt, null
        );
    }
}
