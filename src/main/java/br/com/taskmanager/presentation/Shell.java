package br.com.taskmanager.presentation;

import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.presentation.controller.TaskFormController;
import br.com.taskmanager.presentation.controller.TaskListController;
import br.com.taskmanager.presentation.controller.TrashController;
import br.com.taskmanager.presentation.navigation.Screen;
import br.com.taskmanager.presentation.service.AlertService;
import br.com.taskmanager.presentation.service.FxRunner;
import br.com.taskmanager.presentation.util.InstantConverter;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.EnumMap;
import java.util.Map;

public final class Shell {

    private final Stage stage;
    private final StackPane content = new StackPane();
    private final Map<Screen, Button> navButtons = new EnumMap<>(Screen.class);

    private final TaskListController taskListController;
    private final TaskFormController taskFormController;
    private final TrashController trashController;

    public Shell(
            Stage stage,
            TaskUseCases useCases,
            FxRunner runner,
            AlertService alerts,
            InstantConverter converter
    ) {
        this.stage = stage;
        this.taskListController = new TaskListController(
                useCases.list(),
                useCases.update(),
                useCases.delete(),
                runner,
                alerts,
                converter,
                this::showCreate,
                this::showEdit
        );
        this.taskFormController = new TaskFormController(
                useCases.create(),
                useCases.update(),
                useCases.show(),
                runner,
                alerts,
                converter,
                this::showTasks,
                this::showTasks
        );
        this.trashController = new TrashController(
                useCases.list(),
                useCases.restore(),
                runner,
                alerts,
                converter
        );
        build();
    }

    public void show() {
        stage.show();
        showTasks();
    }

    private void build() {
        content.getStyleClass().add("content-area");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setLeft(buildSidebar());
        root.setCenter(content);
        root.setBottom(buildStatusBar());

        Scene scene = new Scene(root, 1200, 780);
        java.net.URL css = Shell.class.getResource("/css/app.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setScene(scene);
        stage.setTitle("Task Manager");
        stage.setMinWidth(1000);
        stage.setMinHeight(680);
    }

    private VBox buildSidebar() {
        Label brand = new Label("Task Manager");
        brand.getStyleClass().add("brand");

        Label tasksSection = new Label("TAREFAS");
        tasksSection.getStyleClass().add("nav-section");

        Button listButton = navButton("Listar tarefas", Screen.TASKS, this::showTasks);

        Button newButton = new Button("+ Nova tarefa");
        newButton.getStyleClass().addAll("nav-button", "nav-action");
        newButton.setMaxWidth(Double.MAX_VALUE);
        newButton.setAlignment(Pos.CENTER_LEFT);
        newButton.setOnAction(event -> showCreate());

        Label trashSection = new Label("LIXEIRA");
        trashSection.getStyleClass().add("nav-section");

        Button trashButton = navButton("Tarefas excluídas", Screen.TRASH, this::showTrash);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label footer = new Label("JavaFX • MySQL");
        footer.getStyleClass().add("sidebar-footer");

        VBox sidebar = new VBox(6, brand, tasksSection, listButton, newButton, trashSection, trashButton, spacer, footer);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(230);
        sidebar.setMinWidth(230);
        return sidebar;
    }

    private Button navButton(String text, Screen screen, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setOnAction(event -> action.run());
        navButtons.put(screen, button);
        return button;
    }

    private HBox buildStatusBar() {
        Circle dot = new Circle(4, Color.web("#22c55e"));
        Label database = new Label("MySQL conectado");
        Label hint = new Label("Duplo clique edita • Selecione uma tarefa para ver os detalhes");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(8, dot, database, spacer, hint);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("status-bar");
        return bar;
    }

    private void showTasks() {
        content.getChildren().setAll(taskListController.getView());
        setActive(Screen.TASKS);
        taskListController.refresh();
    }

    private void showCreate() {
        taskFormController.startCreate();
        content.getChildren().setAll(taskFormController.getView());
        setActive(null);
    }

    private void showEdit(Task task) {
        taskFormController.startEdit(task);
        content.getChildren().setAll(taskFormController.getView());
        setActive(null);
    }

    private void showTrash() {
        content.getChildren().setAll(trashController.getView());
        setActive(Screen.TRASH);
        trashController.refresh();
    }

    private void setActive(Screen active) {
        navButtons.forEach((screen, button) -> {
            button.getStyleClass().remove("active");
            if (screen == active) {
                button.getStyleClass().add("active");
            }
        });
    }
}
