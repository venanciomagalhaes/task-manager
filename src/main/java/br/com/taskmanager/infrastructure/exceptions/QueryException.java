package br.com.taskmanager.infrastructure.exceptions;

public class QueryException extends InfrastructureException {

    public QueryException(String message, Throwable throwable){
        super(message, throwable);
    }
}
