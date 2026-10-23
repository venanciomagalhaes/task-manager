package br.com.taskmanager.presentation.component;

import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.presentation.util.InstantConverter;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public final class TaskDetailPane extends VBox {

    private final InstantConverter converter;
    private final Label name = new Label();
    private final Label description = new Label();
    private final StatusBadge status = new StatusBadge();
    private final Label deadline = new Label();
    private final Label createdAt = new Label();
    private final Label updatedAt = new Label();
    private final Label finishedAt = new Label();
    private final Label deletedAt = new Label();
    private final Label hint = new Label("Selecione uma tarefa para ver os detalhes.");

    public TaskDetailPane(InstantConverter converter) {
        this.converter = converter;
        getStyleClass().addAll("card", "detail-pane");
        setSpacing(12);
        setPadding(new Insets(18));
        setPrefWidth(330);
        setMinWidth(300);

        name.getStyleClass().add("detail-title");
        name.setWrapText(true);
        description.getStyleClass().add("detail-description");
        description.setWrapText(true);
        hint.getStyleClass().add("muted-label");
        hint.setWrapText(true);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(8);
        addRow(grid, 0, "Prazo", deadline);
        addRow(grid, 1, "Criada em", createdAt);
        addRow(grid, 2, "Atualizada em", updatedAt);
        addRow(grid, 3, "Concluída em", finishedAt);
        addRow(grid, 4, "Excluída em", deletedAt);

        getChildren().addAll(name, status, description, hint, grid);
        VBox.setVgrow(hint, Priority.ALWAYS);
        setTask(null);
    }

    public void setTask(Task task) {
        if (task == null) {
            name.setText("Nenhuma tarefa selecionada");
            status.setStatus(null);
            description.setText("");
            deadline.setText("—");
            createdAt.setText("—");
            updatedAt.setText("—");
            finishedAt.setText("—");
            deletedAt.setText("—");
            hint.setVisible(true);
            hint.setManaged(true);
            return;
        }
        name.setText(task.getName());
        status.setStatus(task.getStatus());
        description.setText(task.getDescription() == null || task.getDescription().isBlank()
                ? "Sem descrição."
                : task.getDescription());
        deadline.setText(converter.format(task.getDeadline()));
        createdAt.setText(converter.format(task.getCreatedAt()));
        updatedAt.setText(converter.format(task.getUpdatedAt()));
        finishedAt.setText(converter.format(task.getFinishedAt()));
        deletedAt.setText(converter.format(task.getDeletedAt()));
        hint.setVisible(false);
        hint.setManaged(false);
    }

    private void addRow(GridPane grid, int row, String label, Label value) {
        Label caption = new Label(label);
        caption.getStyleClass().add("detail-caption");
        value.getStyleClass().add("detail-value");
        value.setWrapText(true);
        grid.add(caption, 0, row);
        grid.add(value, 1, row);
    }
}
