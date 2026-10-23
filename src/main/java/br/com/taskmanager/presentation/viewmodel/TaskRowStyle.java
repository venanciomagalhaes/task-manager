package br.com.taskmanager.presentation.viewmodel;

import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.domain.enums.TaskStatus;

import java.time.Instant;

public enum TaskRowStyle {

    COMPLETED("row-completed"),
    OVERDUE("row-overdue"),
    NONE(null);

    private final String styleClass;

    TaskRowStyle(String styleClass) {
        this.styleClass = styleClass;
    }

    public String styleClass() {
        return styleClass;
    }

    public static TaskRowStyle of(Task task, Instant now) {
        if (task == null) {
            return NONE;
        }
        if (task.getStatus() == TaskStatus.COMPLETED) {
            return COMPLETED;
        }
        boolean active = task.getDeletedAt() == null && task.getStatus() != TaskStatus.CANCELED;
        if (active && task.getDeadline().isBefore(now)) {
            return OVERDUE;
        }
        return NONE;
    }
}
