package br.com.taskmanager.presentation.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LocalDateConverterTest {

    private final LocalDateConverter converter = new LocalDateConverter(Locale.forLanguageTag("pt-BR"));

    @Test
    @DisplayName("formata data no padrão dd/MM/yyyy")
    void formataData() {
        assertEquals("02/01/2026", converter.toString(LocalDate.of(2026, 1, 2)));
    }

    @Test
    @DisplayName("converte texto dd/MM/yyyy para LocalDate")
    void converteTexto() {
        assertEquals(LocalDate.of(2026, 1, 2), converter.fromString("02/01/2026"));
        assertEquals(LocalDate.of(2026, 1, 2), converter.fromString(" 02/01/2026 "));
    }

    @Test
    @DisplayName("aceita 29 de fevereiro em ano bissexto")
    void aceitaAnoBissexto() {
        assertEquals(LocalDate.of(2024, 2, 29), converter.fromString("29/02/2024"));
    }

    @Test
    @DisplayName("retorna nulo para data inválida")
    void rejeitaDataInvalida() {
        assertNull(converter.fromString("31/02/2026"));
        assertNull(converter.fromString("29/02/2025"));
        assertNull(converter.fromString("2026-01-02"));
        assertNull(converter.fromString("02/01/26"));
    }

    @Test
    @DisplayName("retorna nulo para texto vazio")
    void textoVazio() {
        assertNull(converter.fromString(null));
        assertNull(converter.fromString(""));
        assertNull(converter.fromString("   "));
    }

    @Test
    @DisplayName("formata valor nulo como texto vazio")
    void formataNulo() {
        assertEquals("", converter.toString(null));
    }
}
