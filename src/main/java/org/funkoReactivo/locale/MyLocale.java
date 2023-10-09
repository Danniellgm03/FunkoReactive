package org.funkoReactivo.locale;

import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class MyLocale {
    private final Locale localeEs = new Locale("es", "ES");

    public static String toLocalDate(LocalDate date) {
        return date.format(
                DateTimeFormatter
                        .ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault())
        );
    }

    public static String toLocalDateTime(LocalDateTime dateTime) {
        return dateTime.format(
                DateTimeFormatter
                        .ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locale.getDefault())
        );
    }

    public static String toLocalMoney(double money) {
        return new String(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(money).getBytes(StandardCharsets.UTF_8));
    }

    public static String toLocalNumber(double number) {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(number);
    }
}
