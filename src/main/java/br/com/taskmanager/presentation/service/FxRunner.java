package br.com.taskmanager.presentation.service;

import javafx.application.Platform;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public final class FxRunner {

    private final Executor executor;

    public FxRunner(Executor executor) {
        this.executor = executor;
    }

    public <T> void run(Callable<T> action, Consumer<T> onSuccess, Consumer<Throwable> onError) {
        executor.execute(() -> {
            try {
                T result = action.call();
                Platform.runLater(() -> onSuccess.accept(result));
            } catch (Throwable error) {
                Platform.runLater(() -> onError.accept(error));
            }
        });
    }
}
