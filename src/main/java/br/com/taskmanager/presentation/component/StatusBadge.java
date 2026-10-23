package br.com.taskmanager.presentation.component;

import br.com.taskmanager.domain.enums.TaskStatus;
import br.com.taskmanager.presentation.util.StatusText;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;

public final class StatusBadge extends HBox {

    private static final String[] ALL = {
            "status-pending", "status-in-progress", "status-completed", "status-canceled", "status-neutral"
    };

    private final Circle dot = new Circle(3.5);
    private final Label label = new Label();

    public StatusBadge() {
        super(6);
        setAlignment(Pos.CENTER);
        setMaxWidth(Region.USE_PREF_SIZE);
        setMinWidth(Region.USE_PREF_SIZE);
        getStyleClass().add("status-badge");
        dot.getStyleClass().add("status-dot");
        label.getStyleClass().add("status-text");
        getChildren().addAll(dot, label);
        setStatus(null);
    }

    public void setStatus(TaskStatus status) {
        getStyleClass().removeAll(ALL);
        getStyleClass().add(StatusText.styleClass(status));
        label.setText(StatusText.label(status));
    }
}
