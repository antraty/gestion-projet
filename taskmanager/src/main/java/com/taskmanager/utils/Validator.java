package com.taskmanager.utils;

public class Validator {
    public static boolean isEmail(String e) {
        return e != null && e.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");
    }

    public static boolean isNotEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }
}