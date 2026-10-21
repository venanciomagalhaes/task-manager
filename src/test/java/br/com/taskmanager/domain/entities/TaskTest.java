package br.com.taskmanager.domain.entities;

import br.com.taskmanager.domain.enums.TaskStatus;
import br.com.taskmanager.domain.exceptions.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskTest {

    private static final UUID UUID_FIXO = UUID.fromString("6f1e9a3c-2b4d-4e8f-9a1b-3c5d7e9f0a2b");
    private static final String NOME = "Estudar";
    private static final String DESCRICAO = "Revisar o capítulo";
    private static final Instant CREATED_AT = Instant.parse("2026-01-01T10:00:00Z");
    private static final Instant UPDATED_AT = CREATED_AT.plusSeconds(30);
    private static final Instant FINISHED_AT = CREATED_AT.plusSeconds(60);
    private static final Instant DELETED_AT = CREATED_AT.plusSeconds(120);
    private static final Instant DEADLINE = CREATED_AT.plus(1, ChronoUnit.DAYS);

    private Task validTask() {
        return new Task(UUID_FIXO, NOME, DESCRICAO, TaskStatus.PENDING, DEADLINE, null, CREATED_AT, UPDATED_AT, null);
    }

    private Task completedTask() {
        return new Task(UUID_FIXO, NOME, DESCRICAO, TaskStatus.COMPLETED, DEADLINE, FINISHED_AT, CREATED_AT, UPDATED_AT, null);
    }

    private Task deletedTask() {
        return new Task(UUID_FIXO, NOME, DESCRICAO, TaskStatus.PENDING, DEADLINE, null, CREATED_AT, DELETED_AT, DELETED_AT);
    }

    @Nested
    @DisplayName("Construtor com dados válidos")
    class ConstrutorComDadosValidos {

        @Test
        @DisplayName("cria tarefa PENDING sem finishedAt")
        void criaTarefaPending() {
            Task task = validTask();

            assertEquals(UUID_FIXO, task.getUuid());
            assertEquals(NOME, task.getName());
            assertEquals(DESCRICAO, task.getDescription());
            assertEquals(TaskStatus.PENDING, task.getStatus());
            assertEquals(DEADLINE, task.getDeadline());
            assertNull(task.getFinishedAt());
            assertEquals(CREATED_AT, task.getCreatedAt());
            assertEquals(UPDATED_AT, task.getUpdatedAt());
            assertNull(task.getDeletedAt());
        }

        @Test
        @DisplayName("cria tarefa COMPLETED com finishedAt")
        void criaTarefaCompleted() {
            Task task = completedTask();

            assertEquals(TaskStatus.COMPLETED, task.getStatus());
            assertEquals(FINISHED_AT, task.getFinishedAt());
        }

        @Test
        @DisplayName("cria tarefa já deletada com deletedAt")
        void criaTarefaDeletada() {
            Task task = deletedTask();

            assertEquals(DELETED_AT, task.getDeletedAt());
        }

        @Test
        @DisplayName("aceita description nula")
        void aceitaDescriptionNula() {
            Task task = new Task(UUID_FIXO, NOME, null, TaskStatus.PENDING, DEADLINE, null, CREATED_AT, UPDATED_AT, null);

            assertNull(task.getDescription());
        }
    }

    @Nested
    @DisplayName("Validações de campos obrigatórios")
    class CamposObrigatorios {

        @Test
        @DisplayName("não deve criar tarefa sem uuid")
        void rejeitaUuidNulo() {
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> new Task(null, NOME, DESCRICAO, TaskStatus.PENDING, DEADLINE, null, CREATED_AT, UPDATED_AT, null));

            assertEquals("O campo uuid é obrigatório", ex.getMessage());
        }

        @Test
        @DisplayName("não deve criar tarefa sem status")
        void rejeitaStatusNulo() {
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> new Task(UUID_FIXO, NOME, DESCRICAO, null, DEADLINE, null, CREATED_AT, UPDATED_AT, null));

            assertEquals("O campo status é obrigatório", ex.getMessage());
        }

        @Test
        @DisplayName("não deve criar tarefa sem createdAt")
        void rejeitaCreatedAtNulo() {
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> new Task(UUID_FIXO, NOME, DESCRICAO, TaskStatus.PENDING, DEADLINE, null, null, UPDATED_AT, null));

            assertEquals("O campo createdAt é obrigatório", ex.getMessage());
        }

        @Test
        @DisplayName("não deve criar tarefa sem updatedAt")
        void rejeitaUpdatedAtNulo() {
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> new Task(UUID_FIXO, NOME, DESCRICAO, TaskStatus.PENDING, DEADLINE, null, CREATED_AT, null, null));

            assertEquals("O campo updatedAt é obrigatório", ex.getMessage());
        }

        @Test
        @DisplayName("não deve criar tarefa sem deadline")
        void rejeitaDeadlineNulo() {
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> new Task(UUID_FIXO, NOME, DESCRICAO, TaskStatus.PENDING, null, null, CREATED_AT, UPDATED_AT, null));

            assertEquals("O campo deadline é obrigatório", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Validação de name")
    class Nome {

        @Test
        @DisplayName("não deve criar tarefa com name nulo")
        void rejeitaNameNulo() {
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> new Task(UUID_FIXO, null, DESCRICAO, TaskStatus.PENDING, DEADLINE, null, CREATED_AT, UPDATED_AT, null));

            assertEquals("O campo name é obrigatório e não pode estar em branco", ex.getMessage());
        }

        @ParameterizedTest(name = "[{index}] name = \"{0}\"")
        @ValueSource(strings = {"", " "})
        @DisplayName("não deve criar tarefa com name em branco")
        void rejeitaNameEmBranco(String name) {
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> new Task(UUID_FIXO, name, DESCRICAO, TaskStatus.PENDING, DEADLINE, null, CREATED_AT, UPDATED_AT, null));

            assertEquals("O campo name é obrigatório e não pode estar em branco", ex.getMessage());
        }

        @Test
        @DisplayName("aceita name com exatamente 255 caracteres")
        void aceitaNameCom255Caracteres() {
            String name = "a".repeat(255);

            Task task = new Task(UUID_FIXO, name, DESCRICAO, TaskStatus.PENDING, DEADLINE, null, CREATED_AT, UPDATED_AT, null);

            assertEquals(name, task.getName());
        }

        @Test
        @DisplayName("não deve criar tarefa com name de 256 caracteres")
        void rejeitaNameCom256Caracteres() {
            String name = "a".repeat(256);

            ValidationException ex = assertThrows(ValidationException.class,
                    () -> new Task(UUID_FIXO, name, DESCRICAO, TaskStatus.PENDING, DEADLINE, null, CREATED_AT, UPDATED_AT, null));

            assertEquals("O campo name deve ter no máximo 255 caracteres", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Validação de datas")
    class Datas {

        @Test
        @DisplayName("não deve criar tarefa com deadline anterior a createdAt")
        void rejeitaDeadlineAnteriorACreatedAt() {
            Instant deadline = CREATED_AT.minusSeconds(1);

            ValidationException ex = assertThrows(ValidationException.class,
                    () -> new Task(UUID_FIXO, NOME, DESCRICAO, TaskStatus.PENDING, deadline, null, CREATED_AT, UPDATED_AT, null));

            assertEquals("O campo deadline não pode ser anterior à data de criação (createdAt)", ex.getMessage());
        }

        @Test
        @DisplayName("aceita deadline igual a createdAt")
        void aceitaDeadlineIgualACreatedAt() {
            Task task = new Task(UUID_FIXO, NOME, DESCRICAO, TaskStatus.PENDING, CREATED_AT, null, CREATED_AT, UPDATED_AT, null);

            assertEquals(CREATED_AT, task.getDeadline());
        }

        @Test
        @DisplayName("não deve criar tarefa com updatedAt anterior a createdAt")
        void rejeitaUpdatedAtAnteriorACreatedAt() {
            Instant updatedAt = CREATED_AT.minusSeconds(1);

            ValidationException ex = assertThrows(ValidationException.class,
                    () -> new Task(UUID_FIXO, NOME, DESCRICAO, TaskStatus.PENDING, DEADLINE, null, CREATED_AT, updatedAt, null));

            assertEquals("O campo updatedAt não pode ser anterior à data de criação (createdAt)", ex.getMessage());
        }

        @Test
        @DisplayName("não deve criar tarefa com deletedAt anterior a createdAt")
        void rejeitaDeletedAtAnteriorACreatedAt() {
            Instant deletedAt = CREATED_AT.minusSeconds(1);

            ValidationException ex = assertThrows(ValidationException.class,
                    () -> new Task(UUID_FIXO, NOME, DESCRICAO, TaskStatus.PENDING, DEADLINE, null, CREATED_AT, UPDATED_AT, deletedAt));

            assertEquals("O campo deletedAt não pode ser anterior à data de criação (createdAt)", ex.getMessage());
        }

        @Test
        @DisplayName("não deve criar tarefa com finishedAt anterior a createdAt")
        void rejeitaFinishedAtAnteriorACreatedAt() {
            Instant finishedAt = CREATED_AT.minusSeconds(1);

            ValidationException ex = assertThrows(ValidationException.class,
                    () -> new Task(UUID_FIXO, NOME, DESCRICAO, TaskStatus.COMPLETED, DEADLINE, finishedAt, CREATED_AT, UPDATED_AT, null));

            assertEquals("O campo finishedAt não pode ser anterior à data de criação (createdAt)", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Regra do finishedAt")
    class RegraDoFinishedAt {

        @Test
        @DisplayName("não deve criar tarefa COMPLETED sem finishedAt")
        void rejeitaCompletedSemFinishedAt() {
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> new Task(UUID_FIXO, NOME, DESCRICAO, TaskStatus.COMPLETED, DEADLINE, null, CREATED_AT, UPDATED_AT, null));

            assertEquals("O campo finishedAt é obrigatório quando o status é COMPLETED", ex.getMessage());
        }

        @ParameterizedTest(name = "[{index}] status = {0}")
        @EnumSource(value = TaskStatus.class, names = "COMPLETED", mode = EnumSource.Mode.EXCLUDE)
        @DisplayName("não deve criar tarefa não COMPLETED com finishedAt")
        void rejeitaFinishedAtEmStatusNaoCompleted(TaskStatus status) {
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> new Task(UUID_FIXO, NOME, DESCRICAO, status, DEADLINE, FINISHED_AT, CREATED_AT, UPDATED_AT, null));

            assertEquals("O campo finishedAt só pode ser preenchido quando o status é COMPLETED", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Mudança de status")
    class MudancaDeStatus {

        @Test
        @DisplayName("start altera o status para IN_PROGRESS e atualiza updatedAt")
        void startAlteraStatusEAtualizaUpdatedAt() {
            Task task = validTask();
            Instant before = Instant.now();

            task.start();

            assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
            assertFalse(task.getUpdatedAt().isBefore(before));
            assertNull(task.getFinishedAt());
        }

        @Test
        @DisplayName("finish preenche finishedAt igual a updatedAt")
        void finishPreencheFinishedAtIgualAUpdatedAt() {
            Task task = validTask();
            Instant before = Instant.now();

            task.finish();

            assertEquals(TaskStatus.COMPLETED, task.getStatus());
            assertEquals(task.getUpdatedAt(), task.getFinishedAt());
            assertFalse(task.getFinishedAt().isBefore(before));
        }

        @Test
        @DisplayName("sair de COMPLETED limpa finishedAt")
        void sairDeCompletedLimpaFinishedAt() {
            Task task = validTask();
            task.finish();

            task.start();

            assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
            assertNull(task.getFinishedAt());
        }

        @Test
        @DisplayName("reabre tarefa CANCELED")
        void reabreTarefaCanceled() {
            Task task = validTask();
            task.cancel();

            task.pending();

            assertEquals(TaskStatus.PENDING, task.getStatus());
            assertNull(task.getFinishedAt());
        }

        @Test
        @DisplayName("não deve alterar para o mesmo status e mantém updatedAt")
        void rejeitaMesmoStatusEMantemUpdatedAt() {
            Task task = validTask();
            task.start();
            Instant updatedAt = task.getUpdatedAt();

            ValidationException ex = assertThrows(ValidationException.class, task::start);

            assertEquals("A tarefa já está com o status IN_PROGRESS", ex.getMessage());
            assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
            assertEquals(updatedAt, task.getUpdatedAt());
        }

        @Test
        @DisplayName("não deve finalizar tarefa deletada")
        void rejeitaFinishEmTarefaDeletada() {
            Task task = validTask();
            task.delete();

            ValidationException ex = assertThrows(ValidationException.class, task::finish);

            assertEquals("Não é possível alterar o status de uma tarefa deletada. Restaure a tarefa antes.", ex.getMessage());
            assertEquals(TaskStatus.PENDING, task.getStatus());
            assertNull(task.getFinishedAt());
        }
    }

    @Nested
    @DisplayName("Soft delete")
    class SoftDelete {

        @Test
        @DisplayName("delete preenche deletedAt e updatedAt com o mesmo instante")
        void deletePreencheDeletedAtEUpdatedAt() {
            Task task = validTask();
            Instant before = Instant.now();

            task.delete();

            assertEquals(task.getUpdatedAt(), task.getDeletedAt());
            assertFalse(task.getDeletedAt().isBefore(before));
        }

        @Test
        @DisplayName("não deve deletar duas vezes e mantém o deletedAt original")
        void rejeitaDeleteDuplicado() {
            Task task = validTask();
            task.delete();
            Instant deletedAt = task.getDeletedAt();

            ValidationException ex = assertThrows(ValidationException.class, task::delete);

            assertEquals("A tarefa já está deletada", ex.getMessage());
            assertEquals(deletedAt, task.getDeletedAt());
        }

        @Test
        @DisplayName("restore limpa deletedAt e atualiza updatedAt")
        void restoreLimpaDeletedAtEAtualizaUpdatedAt() {
            Task task = deletedTask();
            Instant before = Instant.now();

            task.restore();

            assertNull(task.getDeletedAt());
            assertFalse(task.getUpdatedAt().isBefore(before));
        }

        @Test
        @DisplayName("não deve restaurar tarefa não deletada")
        void rejeitaRestoreEmTarefaNaoDeletada() {
            Task task = validTask();

            ValidationException ex = assertThrows(ValidationException.class, task::restore);

            assertEquals("A tarefa não está deletada, portanto não há o que restaurar", ex.getMessage());
        }

        @Test
        @DisplayName("volta a aceitar mudança de status após delete e restore")
        void aceitaMudancaDeStatusAposRestore() {
            Task task = validTask();
            task.delete();
            task.restore();

            task.start();

            assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
            assertNull(task.getDeletedAt());
        }
    }

    @Nested
    @DisplayName("Igualdade")
    class Igualdade {

        @Test
        @DisplayName("tarefas com o mesmo uuid e nomes diferentes são iguais")
        void iguaisComMesmoUuid() {
            Task task = validTask();
            Task outra = new Task(UUID_FIXO, "Outro nome", "Outra descrição", TaskStatus.COMPLETED, DEADLINE, FINISHED_AT, CREATED_AT, UPDATED_AT, null);

            assertEquals(task, outra);
        }

        @Test
        @DisplayName("tarefas com uuid diferentes não são iguais")
        void diferentesComUuidDiferente() {
            Task task = validTask();
            Task outra = new Task(UUID.randomUUID(), NOME, DESCRICAO, TaskStatus.PENDING, DEADLINE, null, CREATED_AT, UPDATED_AT, null);

            assertNotEquals(task, outra);
        }

        @Test
        @DisplayName("hashCode é igual para o mesmo uuid")
        void hashCodeIgualParaMesmoUuid() {
            Task task = validTask();
            Task outra = new Task(UUID_FIXO, "Outro nome", null, TaskStatus.CANCELED, DEADLINE, null, CREATED_AT, UPDATED_AT, null);

            assertEquals(task.hashCode(), outra.hashCode());
        }
    }
}
