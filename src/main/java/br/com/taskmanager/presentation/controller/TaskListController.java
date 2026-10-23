package br.com.taskmanager.presentation.controller;

import br.com.taskmanager.application.dtos.UpdateTaskDto;
import br.com.taskmanager.application.usecases.DeleteTaskUseCase;
import br.com.taskmanager.application.usecases.ListTasksUseCase;
import br.com.taskmanager.application.usecases.UpdateTaskUseCase;
import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.domain.enums.TaskStatus;
import br.com.taskmanager.presentation.component.TaskColumns;
import br.com.taskmanager.presentation.component.TaskDetailPane;
import br.com.taskmanager.presentation.component.TaskRows;
import br.com.taskmanager.presentation.service.AlertService;
import br.com.taskmanager.presentation.service.FxRunner;
import br.com.taskmanager.presentation.util.InstantConverter;
import br.com.taskmanager.presentation.viewmodel.StatusFilter;
import br.com.taskmanager.presentation.viewmodel.TaskListViewModel;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.function.Consumer;

public final class TaskListController {

    private final ListTasksUseCase listTasks;
    private final UpdateTaskUseCase updateTask;
    private final DeleteTaskUseCase deleteTask;
    private final FxRunner runner;
    private final AlertService alerts;
    private final Runnable onNew;
    private final Consumer<Task> onEdit;

    private final TaskListViewModel viewModel = new TaskListViewModel();
    private final TableView<Task> table = new TableView<>();
    private final TextField searchField = new TextField();
    private final ComboBox<StatusFilter> statusFilter = new ComboBox<>();
    private final TaskDetailPane detailPane;
    private final Button editButton = new Button("Editar");
    private final Button startButton = new Button("Iniciar");
    private final Button completeButton = new Button("Concluir");
    private final Button cancelButton = new Button("Cancelar");
    private final Button deleteButton = new Button("Excluir");
    private final Button refreshButton = new Button("Atualizar");
    private final Button newButton = new Button("Nova tarefa");
    private final VBox root = new VBox(16);
    private final Timeline overdueRefresh = new Timeline(new KeyFrame(Duration.seconds(30), event -> table.refresh()));

    private boolean busy;

    public TaskListController(
            ListTasksUseCase listTasks,
            UpdateTaskUseCase updateTask,
            DeleteTaskUseCase deleteTask,
            FxRunner runner,
            AlertService alerts,
            InstantConverter converter,
            Runnable onNew,
            Consumer<Task> onEdit
    ) {
        this.listTasks = listTasks;
        this.updateTask = updateTask;
        this.deleteTask = deleteTask;
        this.runner = runner;
        this.alerts = alerts;
        this.onNew = onNew;
        this.onEdit = onEdit;
        this.detailPane = new TaskDetailPane(converter);
        build(converter);
    }

    public Node getView() {
        return root;
    }

    public void refresh() {
        setBusy(true);
        runner.run(
                () -> listTasks.handle(false),
                tasks -> {
                    viewModel.setTasks(tasks);
                    applyFilters();
                    setBusy(false);
                },
                error -> {
                    setBusy(false);
                    alerts.showError(error);
                }
        );
    }

    private void build(InstantConverter converter) {
        Label title = new Label("Tarefas");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Gerencie as tarefas ativas. Itens excluídos ficam na lixeira.");
        subtitle.getStyleClass().add("page-subtitle");

        searchField.setPromptText("Buscar por nome");
        searchField.setPrefWidth(260);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.setSearch(newValue);
            applyFilters();
        });

        statusFilter.getItems().setAll(StatusFilter.values());
        statusFilter.getSelectionModel().select(StatusFilter.ALL);
        statusFilter.setPrefWidth(180);
        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.setStatusFilter(newValue == null ? null : newValue.status());
            applyFilters();
        });

        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(event -> refresh());
        newButton.getStyleClass().add("primary-button");
        newButton.setOnAction(event -> onNew.run());

        HBox toolbar = new HBox(8, searchField, statusFilter, spacer(), refreshButton, newButton);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("toolbar");

        table.getColumns().setAll(
                TaskColumns.name(),
                TaskColumns.status(),
                TaskColumns.date("Prazo", Task::getDeadline, converter),
                TaskColumns.finishedAt(converter),
                TaskColumns.date("Criada em", Task::getCreatedAt, converter)
        );
        table.setPlaceholder(new Label("Nenhuma tarefa encontrada."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onSelectionChanged(newValue));
        table.setRowFactory(ignored -> {
            TableRow<Task> row = TaskRows.create();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    onEdit.accept(row.getItem());
                }
            });
            return row;
        });

        editButton.getStyleClass().add("secondary-button");
        editButton.setOnAction(event -> {
            Task selected = selectedTask();
            if (selected != null) {
                onEdit.accept(selected);
            }
        });
        startButton.getStyleClass().add("secondary-button");
        startButton.setOnAction(event -> changeStatus(TaskStatus.IN_PROGRESS));
        completeButton.getStyleClass().add("secondary-button");
        completeButton.setOnAction(event -> changeStatus(TaskStatus.COMPLETED));
        cancelButton.getStyleClass().add("secondary-button");
        cancelButton.setOnAction(event -> changeStatus(TaskStatus.CANCELED));
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(event -> deleteSelected());

        HBox actions = new HBox(8, editButton, startButton, completeButton, cancelButton, deleteButton);
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

        Timeline overdueRefresh = new Timeline(new KeyFrame(Duration.seconds(30), event -> table.refresh()));
        overdueRefresh.setCycleCount(Animation.INDEFINITE);
        overdueRefresh.play();

        onSelectionChanged(null);
    }

    private void applyFilters() {
        Task selected = selectedTask();
        table.getItems().setAll(viewModel.visibleTasks());
        if (selected != null) {
            table.getItems().stream()
                    .filter(task -> task.equals(selected))
                    .findFirst()
                    .ifPresent(task -> table.getSelectionModel().select(task));
        }
        if (table.getSelectionModel().getSelectedItem() == null) {
            onSelectionChanged(null);
        }
    }

    private void onSelectionChanged(Task task) {
        detailPane.setTask(task);
        updateActionState(task);
    }

    private void updateActionState(Task task) {
        boolean available = !busy;
        editButton.setDisable(!available || !TaskListViewModel.canEdit(task));
        startButton.setDisable(!available || !TaskListViewModel.canStart(task));
        completeButton.setDisable(!available || !TaskListViewModel.canComplete(task));
        cancelButton.setDisable(!available || !TaskListViewModel.canCancel(task));
        deleteButton.setDisable(!available || !TaskListViewModel.canDelete(task));
    }

    private void setBusy(boolean busy) {
        this.busy = busy;
        searchField.setDisable(busy);
        statusFilter.setDisable(busy);
        refreshButton.setDisable(busy);
        newButton.setDisable(busy);
        updateActionState(selectedTask());
    }

    private Task selectedTask() {
        return table.getSelectionModel().getSelectedItem();
    }

    private void changeStatus(TaskStatus status) {
        Task selected = selectedTask();
        if (selected == null) {
            return;
        }
        UpdateTaskDto dto = new UpdateTaskDto(
                selected.getName(),
                selected.getDescription(),
                status,
                selected.getDeadline()
        );
        setBusy(true);
        runner.run(
                () -> updateTask.handle(selected.getUuid(), dto),
                updated -> refresh(),
                error -> {
                    setBusy(false);
                    alerts.showError(error);
                }
        );
    }

    private void deleteSelected() {
        Task selected = selectedTask();
        if (selected == null) {
            return;
        }
        boolean confirmed = alerts.confirm(
                "Mover tarefa para a lixeira?",
                "A tarefa \"" + selected.getName() + "\" poderá ser restaurada depois."
        );
        if (!confirmed) {
            return;
        }
        setBusy(true);
        runner.run(
                () -> {
                    deleteTask.handle(selected.getUuid());
                    return null;
                },
                ignored -> refresh(),
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
