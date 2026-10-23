package br.com.taskmanager.presentation.util;

import br.com.taskmanager.domain.exceptions.ValidationException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class InstantConverter {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ZoneId zone;

    public InstantConverter(ZoneId zone) {
        this.zone = zone;
    }

    public static InstantConverter systemDefault() {
        return new InstantConverter(ZoneId.systemDefault());
    }

    public Instant toInstant(LocalDate date, String time) {
        if (date == null) {
            throw new ValidationException("Informe a data do prazo");
        }
        if (time == null || time.isBlank()) {
            throw new ValidationException("Informe o horário do prazo");
        }
        try {
            LocalTime localTime = LocalTime.parse(time.trim(), TIME);
            return date.atTime(localTime).atZone(zone).toInstant();
        } catch (DateTimeParseException e) {
            throw new ValidationException("Horário inválido. Use o formato HH:mm");
        }
    }

    public LocalDate toLocalDate(Instant instant) {
        return instant.atZone(zone).toLocalDate();
    }

    public String toTime(Instant instant) {
        return instant.atZone(zone).format(TIME);
    }

    public String format(Instant instant) {
        return instant == null ? "—" : instant.atZone(zone).format(DATE_TIME);
    }

    public String formatDate(Instant instant) {
        return instant == null ? "—" : instant.atZone(zone).format(DATE);
    }
}
