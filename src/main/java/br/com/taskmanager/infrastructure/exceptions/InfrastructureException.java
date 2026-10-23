package br.com.taskmanager.infrastructure.exceptions;

public abstract class InfrastructureException extends RuntimeException {

    InfrastructureException(String message, Throwable throwable){
        super(message, throwable);
    }
    InfrastructureException(String message){
        super(message);
    }
}
