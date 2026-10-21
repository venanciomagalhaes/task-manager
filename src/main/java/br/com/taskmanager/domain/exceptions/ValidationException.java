package br.com.taskmanager.domain.exceptions;

public class ValidationException extends DomainException {
    public ValidationException(String message) {
        super(message);
    }
}
