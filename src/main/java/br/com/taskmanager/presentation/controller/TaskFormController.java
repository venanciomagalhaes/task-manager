package br.com.taskmanager.presentation.controller;

import br.com.taskmanager.application.dtos.CreateTaskDto;
import br.com.taskmanager.application.dtos.UpdateTaskDto;
import br.com.taskmanager.application.exceptions.ApplicationException;
import br.com.taskmanager.application.usecases.CreateTaskUseCase;
import br.com.taskmanager.application.usecases.ShowTaskUseCase;
import br.com.taskmanager.application.usecases.UpdateTaskUseCase;
import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.domain.enums.TaskStatus;
import br.com.taskmanager.domain.exceptions.DomainException;
import br.com.taskmanager.presentation.service.AlertService;
import br.com.taskmanager.presentation.service.FxRunner;
import br.com.taskmanager.presentation.util.InstantConverter;
import br.com.taskmanager.presentation.util.StatusText;
import br.com.taskmanager.presentation.viewmodel.TaskFormViewModel;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.UUID;

public final class TaskFormController {

    private final CreateTaskUseCase createTask;
    private final UpdateTaskUseCase updateTask;
    private final ShowTaskUseCase showTask;
    private final FxRunner runner;
    private final AlertService alerts;
    private final TaskFormViewModel viewModel;
    private final Runnable onSaved;
    private final Runnable onCancel;

    private final TextField nameField = new TextField();
    private final TextArea descriptionField = new TextArea();
    private final DatePicker deadlinePicker = new DatePicker();
    private final TextField timeField = new TextField();
    private final ComboBox<TaskStatus> statusBox = new ComboBox<>();
    private final Label statusLabel = new Label("Status *");
    private final Label errorLabel = new Label();
    private final Label titleLabel = new Label();
    private final Label subtitleLabel = new Label();
    private final Button saveButton = new Button("Salvar");
    private final Button cancelButton = new Button("Cancelar");
    private final VBox root = new VBox();

    private UUID editingId;

    public TaskFormController(
            CreateTaskUseCase createTask,
            UpdateTaskUseCase updateTask,
            ShowTaskUseCase showTask,
            FxRunner runner,
            AlertService alerts,
            InstantConverter converter,
            Runnable onSaved,
            Runnable onCancel
    ) {
        this.createTask = createTask;
        this.updateTask = updateTask;
        this.showTask = showTask;
        this.runner = runner;
        this.alerts = alerts;
        this.viewModel = new TaskFormViewModel(converter);
        this.onSaved = onSaved;
        this.onCancel = onCancel;
        build();
    }

    public Node getView() {
        return root;
    }

    public void startCreate() {
        editingId = null;
        viewModel.reset();
        applyViewModel();
        titleLabel.setText("Nova tarefa");
        subtitleLabel.setText("Preencha os dados abaixo. Campos com * são obrigatórios.");
        setStatusVisible(false);
        clearError();
        setBusy(false);
        nameField.requestFocus();
    }

    public void startEdit(Task task) {
        editingId = task.getUuid();
        titleLabel.setText("Editar tarefa");
        subtitleLabel.setText("Atualize os dados e salve para aplicar as alterações.");
        setStatusVisible(true);
        clearError();
        setBusy(true);
        runner.run(
                () -> showTask.handle(task.getUuid()),
                loaded -> {
                    viewModel.load(loaded);
                    applyViewModel();
                    setBusy(false);
                    nameField.requestFocus();
                },
                error -> {
                    setBusy(false);
                    alerts.showError(error);
                    onCancel.run();
                }
        );
    }

    private void build() {
        titleLabel.getStyleClass().add("page-title");
        subtitleLabel.getStyleClass().add("page-subtitle");

        nameField.setPromptText("Ex.: Revisar proposta do cliente");
        descriptionField.setPromptText("Detalhes opcionais da tarefa");
        descriptionField.setPrefRowCount(5);
        descriptionField.setWrapText(true);

        deadlinePicker.setPrefWidth(190);
        timeField.setPromptText("HH:mm");
        timeField.setPrefWidth(100);

        statusBox.getItems().setAll(TaskStatus.values());
        statusBox.setPrefWidth(220);
        statusBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(TaskStatus status) {
                return status == null ? "" : StatusText.label(status);
            }

            @Override
            public TaskStatus fromString(String value) {
                return null;
            }
        });

        HBox deadlineBox = new HBox(8, deadlinePicker, timeField);
        deadlineBox.setAlignment(Pos.CENTER_LEFT);

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);
        grid.add(formLabel("Nome *"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(formLabel("Descrição"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(formLabel("Prazo *"), 0, 2);
        grid.add(deadlineBox, 1, 2);
        grid.add(statusLabel, 0, 3);
        grid.add(statusBox, 1, 3);
        statusLabel.getStyleClass().add("form-label");

        ColumnConstraints labels = new ColumnConstraints();
        labels.setMinWidth(120);
        ColumnConstraints fields = new ColumnConstraints();
        fields.setHgrow(Priority.ALWAYS);
        fields.setFillWidth(true);
        grid.getColumnConstraints().addAll(labels, fields);

        errorLabel.getStyleClass().add("error-label");
        errorLabel.setWrapText(true);
        clearError();

        cancelButton.getStyleClass().add("secondary-button");
        cancelButton.setOnAction(event -> onCancel.run());
        cancelButton.setCancelButton(true);
        saveButton.getStyleClass().add("primary-button");
        saveButton.setDefaultButton(true);
        saveButton.setOnAction(event -> save());

        HBox buttons = new HBox(8, cancelButton, saveButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox card = new VBox(18, titleLabel, subtitleLabel, grid, errorLabel, buttons);
        card.getStyleClass().add("card");
        card.setMaxWidth(780);

        root.getChildren().add(card);
        root.getStyleClass().add("content");
        root.setAlignment(Pos.TOP_LEFT);
    }

    private void applyViewModel() {
        nameField.setText(viewModel.getName());
        descriptionField.setText(viewModel.getDescription());
        deadlinePicker.setValue(viewModel.getDeadlineDate());
        timeField.setText(viewModel.getDeadlineTime());
        statusBox.getSelectionModel().select(viewModel.getStatus());
    }

    private void save() {
        clearError();
        try {
            readForm();
            if (editingId == null) {
                CreateTaskDto dto = viewModel.toCreateDto();
                setBusy(true);
                runner.run(
                        () -> createTask.handle(dto),
                        created -> {
                            setBusy(false);
                            onSaved.run();
                        },
                        this::handleFailure
                );
                return;
            }
            UpdateTaskDto dto = viewModel.toUpdateDto();
            UUID uuid = editingId;
            setBusy(true);
            runner.run(
                    () -> updateTask.handle(uuid, dto),
                    updated -> {
                        setBusy(false);
                        onSaved.run();
                    },
                    this::handleFailure
            );
        } catch (RuntimeException error) {
            showFormError(error);
        }
    }

    private void readForm() {
        viewModel.setName(nameField.getText());
        viewModel.setDescription(descriptionField.getText());
        viewModel.setDeadlineDate(deadlinePicker.getValue());
        viewModel.setDeadlineTime(timeField.getText());
        viewModel.setStatus(statusBox.getValue());
    }

    private void handleFailure(Throwable error) {
        setBusy(false);
        showFormError(error);
    }

    private void showFormError(Throwable error) {
        if (error instanceof DomainException || error instanceof ApplicationException) {
            errorLabel.setText(error.getMessage());
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            return;
        }
        alerts.showError(error);
    }

    private void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void setStatusVisible(boolean visible) {
        statusLabel.setVisible(visible);
        statusLabel.setManaged(visible);
        statusBox.setVisible(visible);
        statusBox.setManaged(visible);
    }

    private void setBusy(boolean busy) {
        nameField.setDisable(busy);
        descriptionField.setDisable(busy);
        deadlinePicker.setDisable(busy);
        timeField.setDisable(busy);
        statusBox.setDisable(busy);
        saveButton.setDisable(busy);
        cancelButton.setDisable(busy);
    }

    private Label formLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }
}
