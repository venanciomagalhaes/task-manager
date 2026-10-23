package br.com.taskmanager.presentation.util;

import br.com.taskmanager.domain.enums.TaskStatus;

public final class StatusText {

    private StatusText() {
    }

    public static String label(TaskStatus status) {
        if (status == null) {
            return "—";
        }
        return switch (status) {
            case PENDING -> "Pendente";
            case IN_PROGRESS -> "Em andamento";
            case COMPLETED -> "Concluída";
            case CANCELED -> "Cancelada";
        };
    }

    public static String styleClass(TaskStatus status) {
        if (status == null) {
            return "status-neutral";
        }
        return switch (status) {
            case PENDING -> "status-pending";
            case IN_PROGRESS -> "status-in-progress";
            case COMPLETED -> "status-completed";
            case CANCELED -> "status-canceled";
        };
    }
}
