package br.com.taskmanager.presentation.component;

import br.com.taskmanager.domain.enums.TaskStatus;
import br.com.taskmanager.presentation.util.StatusText;
import javafx.scene.control.Label;

public final class StatusBadge extends Label {

    private static final String[] ALL = {
            "status-pending", "status-in-progress", "status-completed", "status-canceled", "status-neutral"
    };

    public StatusBadge() {
        getStyleClass().add("status-badge");
        setStatus(null);
    }

    public void setStatus(TaskStatus status) {
        getStyleClass().removeAll(ALL);
        getStyleClass().add(StatusText.styleClass(status));
        setText(StatusText.label(status));
    }
}
