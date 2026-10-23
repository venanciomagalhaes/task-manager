package br.com.taskmanager.presentation.component;

import br.com.taskmanager.domain.entities.Task;
import javafx.scene.control.TableRow;

public final class TaskRows {

    private static final String[] STATUS_CLASSES = {
            "row-pending", "row-in-progress", "row-completed", "row-canceled"
    };

    private TaskRows() {
    }

    public static TableRow<Task> create() {
        return new TableRow<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                getStyleClass().removeAll(STATUS_CLASSES);
                if (empty || task == null) {
                    return;
                }
                getStyleClass().add(switch (task.getStatus()) {
                    case PENDING -> "row-pending";
                    case IN_PROGRESS -> "row-in-progress";
                    case COMPLETED -> "row-completed";
                    case CANCELED -> "row-canceled";
                });
            }
        };
    }
}
