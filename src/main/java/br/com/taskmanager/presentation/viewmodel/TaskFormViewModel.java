package br.com.taskmanager.presentation.viewmodel;

import br.com.taskmanager.application.dtos.CreateTaskDto;
import br.com.taskmanager.application.dtos.UpdateTaskDto;
import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.domain.enums.TaskStatus;
import br.com.taskmanager.domain.exceptions.ValidationException;
import br.com.taskmanager.presentation.util.InstantConverter;

import java.time.LocalDate;

public final class TaskFormViewModel {

    private final InstantConverter converter;

    private String name = "";
    private String description = "";
    private LocalDate deadlineDate = LocalDate.now().plusDays(1);
    private String deadlineTime = "18:00";
    private TaskStatus status = TaskStatus.PENDING;

    public TaskFormViewModel(InstantConverter converter) {
        this.converter = converter;
    }

    public void reset() {
        name = "";
        description = "";
        deadlineDate = LocalDate.now().plusDays(1);
        deadlineTime = "18:00";
        status = TaskStatus.PENDING;
    }

    public void load(Task task) {
        name = task.getName();
        description = task.getDescription() == null ? "" : task.getDescription();
        deadlineDate = converter.toLocalDate(task.getDeadline());
        deadlineTime = converter.toTime(task.getDeadline());
        status = task.getStatus();
    }

    public CreateTaskDto toCreateDto() {
        validate();
        return new CreateTaskDto(
                name.trim(),
                blankToNull(description),
                converter.toInstant(deadlineDate, deadlineTime)
        );
    }

    public UpdateTaskDto toUpdateDto() {
        validate();
        if (status == null) {
            throw new ValidationException("Selecione o status da tarefa");
        }
        return new UpdateTaskDto(
                name.trim(),
                blankToNull(description),
                status,
                converter.toInstant(deadlineDate, deadlineTime)
        );
    }

    private void validate() {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Informe o nome da tarefa");
        }
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? "" : name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    public LocalDate getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(LocalDate deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public String getDeadlineTime() {
        return deadlineTime;
    }

    public void setDeadlineTime(String deadlineTime) {
        this.deadlineTime = deadlineTime == null ? "" : deadlineTime;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
