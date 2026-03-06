package com.taskmanager.services;

import java.sql.*;

public class DatabaseService {
    private static DatabaseService instance;
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:taskmanager.db";
    
    private DatabaseService() {
        try {
            connection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    public void initializeDatabase() {
        try (Statement stmt = connection.createStatement()) {
            // Table Utilisateurs
            String createUserTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT UNIQUE NOT NULL, " +
                    "password_hash TEXT NOT NULL" +
                    ")";
            
            // Table Tâches
            String createTaskTable = "CREATE TABLE IF NOT EXISTS tasks (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title TEXT NOT NULL, " +
                    "description TEXT, " +
                    "creation_date TIMESTAMP NOT NULL, " +
                    "due_date DATE, " +
                    "priority TEXT NOT NULL, " +
                    "category TEXT NOT NULL, " +
                    "status TEXT NOT NULL, " +
                    "user_id INTEGER, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id)" +
                    ")";
            
            stmt.execute(createUserTable);
            stmt.execute(createTaskTable);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}