package com.taskmanager.config;

public class DatabaseConfig {
    public static String getJdbcUrl() {
        // Example: jdbc:sqlite:taskmanager.db
        return AppConfig.getOrDefault("db.url", "jdbc:sqlite:taskmanager.db");
    }
}