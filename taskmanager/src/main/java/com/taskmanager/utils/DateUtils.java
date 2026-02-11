package com.taskmanager.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static String format(LocalDate d) {
        if (d == null) return "";
        return d.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}