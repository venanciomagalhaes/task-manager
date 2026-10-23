package br.com.taskmanager.presentation.service;

import br.com.taskmanager.application.exceptions.ApplicationException;
import br.com.taskmanager.domain.exceptions.DomainException;
import br.com.taskmanager.infrastructure.exceptions.InfrastructureException;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public final class AlertService {

    private static final String TITLE = "Task Manager";

    public void showError(Throwable error) {
        if (error instanceof DomainException || error instanceof ApplicationException) {
            show(Alert.AlertType.WARNING, "Não foi possível concluir", error.getMessage());
            return;
        }
        if (error instanceof InfrastructureException) {
            String detail = error.getCause() == null ? null : error.getCause().getMessage();
            String content = error.getMessage() + (detail == null ? "" : "\n\nDetalhe: " + detail);
            show(Alert.AlertType.ERROR, "Falha ao acessar o banco de dados", content);
            return;
        }
        String content = error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage();
        show(Alert.AlertType.ERROR, "Erro inesperado", content);
    }

    public void showInfo(String header, String content) {
        show(Alert.AlertType.INFORMATION, header, content);
    }

    public boolean confirm(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, content, ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle(TITLE);
        alert.setHeaderText(header);
        return alert.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    private void show(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(TITLE);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
