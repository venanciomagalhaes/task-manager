package br.com.taskmanager.presentation.util;

import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

public final class LocalDateConverter extends StringConverter<LocalDate> {

    public static final String FORMAT = "dd/MM/yyyy";
    public static final String FORMAT_HINT = "dd/MM/aaaa";

    private final DateTimeFormatter formatter;

    public LocalDateConverter() {
        this(Locale.forLanguageTag("pt-BR"));
    }

    public LocalDateConverter(Locale locale) {
        this.formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu", locale)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    @Override
    public String toString(LocalDate value) {
        return value == null ? "" : formatter.format(value);
    }

    @Override
    public LocalDate fromString(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(text.trim(), formatter);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }
}
