package com.gestionprojet.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public static String formatDate(LocalDate date) {
        if (date == null) return "Non définie";
        return date.format(DATE_FORMATTER);
    }
    
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "Non définie";
        return dateTime.format(DATE_TIME_FORMATTER);
    }
    
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }
}