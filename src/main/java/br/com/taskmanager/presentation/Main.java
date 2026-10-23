package br.com.taskmanager.presentation;

import br.com.taskmanager.application.repositories.TaskRepository;
import br.com.taskmanager.application.usecases.CreateTaskUseCase;
import br.com.taskmanager.application.usecases.DeleteTaskUseCase;
import br.com.taskmanager.application.usecases.ListTasksUseCase;
import br.com.taskmanager.application.usecases.RestoreTaskUseCase;
import br.com.taskmanager.application.usecases.ShowTaskUseCase;
import br.com.taskmanager.application.usecases.UpdateTaskUseCase;
import br.com.taskmanager.infrastructure.config.MySqlDataSourceFactory;
import br.com.taskmanager.infrastructure.repositories.MySqlTaskRepository;
import br.com.taskmanager.presentation.service.AlertService;
import br.com.taskmanager.presentation.service.FxRunner;
import br.com.taskmanager.presentation.util.InstantConverter;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Main extends Application {

    private DataSource dataSource;
    private ExecutorService executor;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            Dotenv dotenv = Dotenv.load();
            this.dataSource = MySqlDataSourceFactory.create(dotenv);
            this.executor = Executors.newSingleThreadExecutor(runnable -> {
                Thread thread = new Thread(runnable, "taskmanager-ui-worker");
                thread.setDaemon(true);
                return thread;
            });

            TaskRepository repository = new MySqlTaskRepository(dataSource);
            TaskUseCases useCases = new TaskUseCases(
                    new CreateTaskUseCase(repository),
                    new UpdateTaskUseCase(repository),
                    new DeleteTaskUseCase(repository),
                    new RestoreTaskUseCase(repository),
                    new ShowTaskUseCase(repository),
                    new ListTasksUseCase(repository)
            );

            AlertService alerts = new AlertService();
            Shell shell = new Shell(stage, useCases, new FxRunner(executor), alerts, InstantConverter.systemDefault());
            shell.show();
        } catch (RuntimeException error) {
            showStartupError(error);
        }
    }

    @Override
    public void stop() throws Exception {
        if (executor != null) {
            executor.shutdownNow();
        }
        if (dataSource instanceof AutoCloseable closeable) {
            closeable.close();
        }
    }

    private void showStartupError(Throwable error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Task Manager");
        alert.setHeaderText("Não foi possível iniciar a aplicação");
        String detail = error.getCause() == null ? null : error.getCause().getMessage();
        alert.setContentText(error.getMessage() + (detail == null ? "" : "\n\nDetalhe: " + detail));
        alert.showAndWait();
        Platform.exit();
    }
}
