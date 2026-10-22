package br.com.taskmanager.application.exceptions;

import java.util.UUID;

public class TaskNotFoundException extends ApplicationException {
    public TaskNotFoundException(UUID uuid) {
        super("Não foi possível encontrar a task " + uuid.toString());
    }
}
