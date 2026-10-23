package br.com.taskmanager.application.queries;

import java.util.List;

public record PageResult<T>(List<T> items, long totalElements, int page, int size) {

    public PageResult {
        if (page < 0) {
            throw new IllegalArgumentException("A página não pode ser negativa");
        }
        if (size < 1) {
            throw new IllegalArgumentException("O tamanho da página deve ser positivo");
        }
        if (totalElements < 0) {
            throw new IllegalArgumentException("O total de elementos não pode ser negativo");
        }
        items = items == null ? List.of() : List.copyOf(items);
    }

    public int totalPages() {
        return totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
    }

    public boolean hasNext() {
        return page + 1 < totalPages();
    }

    public boolean hasPrevious() {
        return page > 0;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
