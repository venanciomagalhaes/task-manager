package br.com.taskmanager.application.exceptions;

public class TaskNotFoundException extends ApplicationException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
