package br.com.taskmanager.presentation.controller;

import br.com.taskmanager.application.usecases.ListTasksUseCase;
import br.com.taskmanager.application.usecases.RestoreTaskUseCase;
import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.presentation.component.TaskColumns;
import br.com.taskmanager.presentation.component.TaskDetailPane;
import br.com.taskmanager.presentation.service.AlertService;
import br.com.taskmanager.presentation.service.FxRunner;
import br.com.taskmanager.presentation.util.InstantConverter;
import br.com.taskmanager.presentation.viewmodel.TaskListViewModel;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class TrashController {

    private final ListTasksUseCase listTasks;
    private final RestoreTaskUseCase restoreTask;
    private final FxRunner runner;
    private final AlertService alerts;

    private final TableView<Task> table = new TableView<>();
    private final TaskDetailPane detailPane;
    private final Button restoreButton = new Button("Restaurar");
    private final Button refreshButton = new Button("Atualizar");
    private final VBox root = new VBox(16);

    private boolean busy;

    public TrashController(
            ListTasksUseCase listTasks,
            RestoreTaskUseCase restoreTask,
            FxRunner runner,
            AlertService alerts,
            InstantConverter converter
    ) {
        this.listTasks = listTasks;
        this.restoreTask = restoreTask;
        this.runner = runner;
        this.alerts = alerts;
        this.detailPane = new TaskDetailPane(converter);
        build(converter);
    }

    public Node getView() {
        return root;
    }

    public void refresh() {
        setBusy(true);
        runner.run(
                () -> listTasks.handle(true),
                tasks -> {
                    table.getItems().setAll(TaskListViewModel.deletedOnly(tasks));
                    table.getSelectionModel().clearSelection();
                    setBusy(false);
                },
                error -> {
                    setBusy(false);
                    alerts.showError(error);
                }
        );
    }

    private void build(InstantConverter converter) {
        Label title = new Label("Lixeira");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Tarefas excluídas. Restaure para devolvê-las à lista de tarefas ativas.");
        subtitle.getStyleClass().add("page-subtitle");

        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(event -> refresh());

        HBox toolbar = new HBox(8, spacer(), refreshButton);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("toolbar");

        table.getColumns().setAll(
                TaskColumns.name(),
                TaskColumns.status(),
                TaskColumns.date("Prazo", Task::getDeadline, converter),
                TaskColumns.date("Excluída em", Task::getDeletedAt, converter)
        );
        table.setPlaceholder(new Label("A lixeira está vazia."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onSelectionChanged(newValue));

        restoreButton.getStyleClass().add("primary-button");
        restoreButton.setOnAction(event -> restoreSelected());

        HBox actions = new HBox(8, restoreButton);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.getStyleClass().add("toolbar");

        VBox tableCard = new VBox(12, table, actions);
        tableCard.getStyleClass().add("card");
        tableCard.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox center = new HBox(16, tableCard, detailPane);
        HBox.setHgrow(tableCard, Priority.ALWAYS);
        VBox.setVgrow(center, Priority.ALWAYS);

        root.getChildren().addAll(title, subtitle, toolbar, center);
        root.getStyleClass().add("content");
        VBox.setVgrow(center, Priority.ALWAYS);

        onSelectionChanged(null);
    }

    private void onSelectionChanged(Task task) {
        detailPane.setTask(task);
        restoreButton.setDisable(busy || !TaskListViewModel.canRestore(task));
    }

    private void setBusy(boolean busy) {
        this.busy = busy;
        refreshButton.setDisable(busy);
        restoreButton.setDisable(busy || !TaskListViewModel.canRestore(selectedTask()));
    }

    private Task selectedTask() {
        return table.getSelectionModel().getSelectedItem();
    }

    private void restoreSelected() {
        Task selected = selectedTask();
        if (selected == null) {
            return;
        }
        boolean confirmed = alerts.confirm(
                "Restaurar tarefa?",
                "A tarefa \"" + selected.getName() + "\" voltará para a lista de tarefas ativas."
        );
        if (!confirmed) {
            return;
        }
        setBusy(true);
        runner.run(
                () -> restoreTask.handle(selected.getUuid()),
                restored -> {
                    setBusy(false);
                    refresh();
                },
                error -> {
                    setBusy(false);
                    alerts.showError(error);
                }
        );
    }

    private Region spacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
}
