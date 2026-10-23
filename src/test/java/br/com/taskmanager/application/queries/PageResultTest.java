package br.com.taskmanager.application.queries;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PageResultTest {

    @Test
    @DisplayName("calcula total de páginas e navegação")
    void calculaPaginacao() {
        PageResult<String> first = new PageResult<>(List.of("a", "b"), 25, 0, 10);

        assertEquals(3, first.totalPages());
        assertTrue(first.hasNext());
        assertFalse(first.hasPrevious());

        PageResult<String> last = new PageResult<>(List.of("y"), 21, 2, 10);

        assertFalse(last.hasNext());
        assertTrue(last.hasPrevious());
    }

    @Test
    @DisplayName("resultado vazio tem zero páginas")
    void resultadoVazio() {
        PageResult<String> empty = new PageResult<>(List.of(), 0, 0, 10);

        assertEquals(0, empty.totalPages());
        assertFalse(empty.hasNext());
        assertFalse(empty.hasPrevious());
        assertTrue(empty.isEmpty());
    }

    @Test
    @DisplayName("copia a lista recebida")
    void copiaLista() {
        List<String> items = new ArrayList<>(List.of("a"));
        PageResult<String> result = new PageResult<>(items, 1, 0, 10);
        items.add("b");

        assertEquals(List.of("a"), result.items());
        assertThrows(UnsupportedOperationException.class, () -> result.items().add("c"));
    }
}
