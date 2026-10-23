package br.com.taskmanager.application.queries;

public record PageRequest(int page, int size) {

    public PageRequest {
        if (page < 0) {
            throw new IllegalArgumentException("A página não pode ser negativa");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("O tamanho da página deve estar entre 1 e 100");
        }
    }

    public long offset() {
        return (long) page * size;
    }
}
