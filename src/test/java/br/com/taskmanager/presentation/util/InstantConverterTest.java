package br.com.taskmanager.presentation.util;

import br.com.taskmanager.domain.exceptions.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InstantConverterTest {

    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");
    private static final LocalDate DATE = LocalDate.of(2026, 1, 2);
    private static final Instant INSTANT = Instant.parse("2026-01-02T13:30:00Z");

    private final InstantConverter converter = new InstantConverter(ZONE);

    @Test
    @DisplayName("converte data e hora local para Instant")
    void converteParaInstant() {
        assertEquals(INSTANT, converter.toInstant(DATE, "10:30"));
    }

    @Test
    @DisplayName("converte Instant para data e hora local")
    void converteDeInstant() {
        assertEquals(DATE, converter.toLocalDate(INSTANT));
        assertEquals("10:30", converter.toTime(INSTANT));
    }

    @Test
    @DisplayName("formata data e hora para exibição")
    void formataDataEHora() {
        assertEquals("02/01/2026 10:30", converter.format(INSTANT));
        assertEquals("02/01/2026", converter.formatDate(INSTANT));
    }

    @Test
    @DisplayName("usa travessão para valores nulos")
    void formataNulo() {
        assertEquals("—", converter.format(null));
        assertEquals("—", converter.formatDate(null));
    }

    @Test
    @DisplayName("rejeita data ausente")
    void rejeitaDataAusente() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> converter.toInstant(null, "10:30"));

        assertEquals("Informe a data do prazo", exception.getMessage());
    }

    @Test
    @DisplayName("rejeita horário ausente ou inválido")
    void rejeitaHorarioInvalido() {
        assertEquals("Informe o horário do prazo",
                assertThrows(ValidationException.class, () -> converter.toInstant(DATE, " ")).getMessage());
        assertEquals("Horário inválido. Use o formato HH:mm",
                assertThrows(ValidationException.class, () -> converter.toInstant(DATE, "25:00")).getMessage());
    }
}
