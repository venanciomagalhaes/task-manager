package br.com.taskmanager.presentation.viewmodel;

import br.com.taskmanager.domain.enums.TaskStatus;

public enum StatusFilter {

    ALL("Todos os status", null),
    PENDING("Pendente", TaskStatus.PENDING),
    IN_PROGRESS("Em andamento", TaskStatus.IN_PROGRESS),
    COMPLETED("Concluída", TaskStatus.COMPLETED),
    CANCELED("Cancelada", TaskStatus.CANCELED);

    private final String label;
    private final TaskStatus status;

    StatusFilter(String label, TaskStatus status) {
        this.label = label;
        this.status = status;
    }

    public TaskStatus status() {
        return status;
    }

    @Override
    public String toString() {
        return label;
    }
}
