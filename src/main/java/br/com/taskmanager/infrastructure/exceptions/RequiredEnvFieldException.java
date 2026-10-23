package br.com.taskmanager.infrastructure.exceptions;

public class RequiredEnvFieldException extends InfrastructureException {

    public RequiredEnvFieldException(String name) {
        super("A variável de ambiente " + name + " é obrigatória");
    }
}
