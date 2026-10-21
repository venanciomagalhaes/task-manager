package br.com.taskmanager.domain.entities;

import br.com.taskmanager.domain.enums.TaskStatus;
import br.com.taskmanager.domain.exceptions.ValidationException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Task {

    private UUID uuid;
    private String name;
    private String description;
    private TaskStatus status;
    private Instant deadline;
    private Instant finishedAt;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public Task(UUID uuid, String name, String description, TaskStatus status, Instant deadline,
                Instant finishedAt, Instant createdAt, Instant updatedAt, Instant deletedAt) {
        this.setUuid(uuid);
        this.setName(name);
        this.setDescription(description);
        this.setStatus(status);
        this.setCreatedAt(createdAt);
        this.setUpdatedAt(updatedAt);
        this.setDeletedAt(deletedAt);
        this.setDeadline(deadline);
        this.setFinishedAt(finishedAt);
    }

    private void setUuid(UUID uuid) {
        if (uuid == null) {
            throw new ValidationException("O campo uuid é obrigatório");
        }
        this.uuid = uuid;
    }

    private void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("O campo name é obrigatório e não pode estar em branco");
        }
        if (name.length() > 255) {
            throw new ValidationException("O campo name deve ter no máximo 255 caracteres");
        }
        this.name = name;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    private void setStatus(TaskStatus status) {
        if (status == null) {
            throw new ValidationException("O campo status é obrigatório");
        }
        this.status = status;
    }

    private void setDeadline(Instant deadline) {
        if (deadline == null) {
            throw new ValidationException("O campo deadline é obrigatório");
        }
        if (deadline.isBefore(this.createdAt)) {
            throw new ValidationException("O campo deadline não pode ser anterior à data de criação (createdAt)");
        }
        this.deadline = deadline;
    }

    private void setFinishedAt(Instant finishedAt) {
        if (finishedAt == null && this.status == TaskStatus.COMPLETED) {
            throw new ValidationException("O campo finishedAt é obrigatório quando o status é COMPLETED");
        }
        if (finishedAt != null && this.status != TaskStatus.COMPLETED) {
            throw new ValidationException("O campo finishedAt só pode ser preenchido quando o status é COMPLETED");
        }
        if (finishedAt != null && finishedAt.isBefore(this.createdAt)) {
            throw new ValidationException("O campo finishedAt não pode ser anterior à data de criação (createdAt)");
        }
        this.finishedAt = finishedAt;
    }

    private void setCreatedAt(Instant createdAt) {
        if (createdAt == null) {
            throw new ValidationException("O campo createdAt é obrigatório");
        }
        this.createdAt = createdAt;
    }

    private void setUpdatedAt(Instant updatedAt) {
        if (updatedAt == null) {
            throw new ValidationException("O campo updatedAt é obrigatório");
        }
        if (updatedAt.isBefore(this.createdAt)) {
            throw new ValidationException("O campo updatedAt não pode ser anterior à data de criação (createdAt)");
        }
        this.updatedAt = updatedAt;
    }

    private void setDeletedAt(Instant deletedAt) {
        if (deletedAt != null && deletedAt.isBefore(this.createdAt)) {
            throw new ValidationException("O campo deletedAt não pode ser anterior à data de criação (createdAt)");
        }
        this.deletedAt = deletedAt;
    }

    public UUID getUuid()          { return uuid; }
    public String getName()        { return name; }
    public String getDescription() { return description; }
    public TaskStatus getStatus()  { return status; }
    public Instant getDeadline()   { return deadline; }
    public Instant getFinishedAt() { return finishedAt; }
    public Instant getCreatedAt()  { return createdAt; }
    public Instant getUpdatedAt()  { return updatedAt; }
    public Instant getDeletedAt()  { return deletedAt; }

    public void pending() { changeStatus(TaskStatus.PENDING); }
    public void start()   { changeStatus(TaskStatus.IN_PROGRESS); }
    public void cancel()  { changeStatus(TaskStatus.CANCELED); }
    public void finish()  { changeStatus(TaskStatus.COMPLETED); }

    private void changeStatus(TaskStatus next) {
        if (next == null) {
            throw new ValidationException("O novo status é obrigatório");
        }
        if (this.deletedAt != null) {
            throw new ValidationException("Não é possível alterar o status de uma tarefa deletada. Restaure a tarefa antes.");
        }
        if (this.status == next) {
            throw new ValidationException("A tarefa já está com o status " + next);
        }
        Instant now = Instant.now();
        this.status = next;
        this.updatedAt = now;
        this.finishedAt = (next == TaskStatus.COMPLETED) ? now : null;
    }

    public void delete() {
        if (this.deletedAt != null) {
            throw new ValidationException("A tarefa já está deletada");
        }
        Instant now = Instant.now();
        this.deletedAt = now;
        this.updatedAt = now;
    }

    public void restore() {
        if (this.deletedAt == null) {
            throw new ValidationException("A tarefa não está deletada, portanto não há o que restaurar");
        }
        this.deletedAt = null;
        this.updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Task task)) return false;
        return Objects.equals(uuid, task.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }

    @Override
    public String toString() {
        return "Task{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", deadline=" + deadline +
                ", finishedAt=" + finishedAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                '}';
    }
}