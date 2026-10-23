package br.com.taskmanager.application.queries;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PageRequestTest {

    @Test
    @DisplayName("calcula o offset da página")
    void calculaOffset() {
        assertEquals(0L, new PageRequest(0, 10).offset());
        assertEquals(20L, new PageRequest(2, 10).offset());
        assertEquals(2_000_000_000L, new PageRequest(20_000_000, 100).offset());
    }

    @Test
    @DisplayName("rejeita página negativa")
    void rejeitaPaginaNegativa() {
        assertThrows(IllegalArgumentException.class, () -> new PageRequest(-1, 10));
    }

    @Test
    @DisplayName("rejeita tamanho fora do intervalo")
    void rejeitaTamanhoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new PageRequest(0, 0));
        assertThrows(IllegalArgumentException.class, () -> new PageRequest(0, 101));
    }
}
