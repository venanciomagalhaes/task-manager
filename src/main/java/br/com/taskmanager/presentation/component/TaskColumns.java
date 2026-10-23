package br.com.taskmanager.presentation.component;

import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.domain.enums.TaskStatus;
import br.com.taskmanager.presentation.util.InstantConverter;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

import java.time.Instant;
import java.util.function.Function;

public final class TaskColumns {

    private TaskColumns() {
    }

    public static TableColumn<Task, String> name() {
        TableColumn<Task, String> column = new TableColumn<>("Nome");
        column.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        column.setPrefWidth(240);
        return column;
    }

    public static TableColumn<Task, TaskStatus> status() {
        TableColumn<Task, TaskStatus> column = new TableColumn<>("Status");
        column.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getStatus()));
        column.setCellFactory(ignored -> new TableCell<>() {
            private final StatusBadge badge = new StatusBadge();

            @Override
            protected void updateItem(TaskStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                badge.setStatus(item);
                setGraphic(badge);
            }
        });
        column.setPrefWidth(150);
        return column;
    }

    public static TableColumn<Task, Instant> date(
            String title,
            Function<Task, Instant> extractor,
            InstantConverter converter
    ) {
        TableColumn<Task, Instant> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(extractor.apply(data.getValue())));
        column.setCellFactory(ignored -> new TableCell<>() {
            @Override
            protected void updateItem(Instant item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "—" : converter.format(item));
            }
        });
        column.setPrefWidth(160);
        return column;
    }
}
