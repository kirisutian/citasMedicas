package com.christian.commons.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StringCustomUtils {

    private static final DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String quitarAcentos(String texto) {
        return texto.toLowerCase()
                .replace("á", "a").replace("é", "e")
                .replace("í", "i").replace("ó", "o")
                .replace("ú", "u").replace("ü", "u");
    }

    public static String localDateAString(LocalDate localDate) {
        return localDate == null ? null : localDate.format(formato);
    }
}
