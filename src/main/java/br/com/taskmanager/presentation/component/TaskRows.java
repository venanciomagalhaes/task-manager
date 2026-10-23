package br.com.taskmanager.presentation.component;

import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.presentation.viewmodel.TaskRowStyle;
import javafx.scene.control.TableRow;

import java.time.Instant;

public final class TaskRows {

    private static final String[] STYLES = {"row-completed", "row-overdue"};

    private TaskRows() {
    }

    public static TableRow<Task> create() {
        return new TableRow<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                getStyleClass().removeAll(STYLES);
                if (empty || task == null) {
                    return;
                }
                TaskRowStyle style = TaskRowStyle.of(task, Instant.now());
                if (style.styleClass() != null) {
                    getStyleClass().add(style.styleClass());
                }
            }
        };
    }
}
