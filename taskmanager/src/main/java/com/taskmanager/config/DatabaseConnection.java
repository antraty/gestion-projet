package com.taskmanager.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection conn;

    //creation constructeur privée pour ouvrir la connexion
    private DatabaseConnection() throws SQLException {
        String url = DatabaseConfig.getJdbcUrl();
        String user = AppConfig.getOrDefault("db.user", "root");
        String password = AppConfig.getOrDefault("db.password", "");
        conn = DriverManager.getConnection(DatabaseConfig.getJdbcUrl(), user, password);
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return conn;
    }
} 