package com.taskmanager.services;

import java.sql.*;

public class DatabaseService {
    private static DatabaseService instance;
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:taskmanager.db";
    
    private DatabaseService() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connexion à la base de données établie");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion à la base de données: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Impossible de se connecter à la base de données", e);
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
            String createUserTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT UNIQUE NOT NULL, " +
                    "password_hash TEXT NOT NULL" +
                    ")";
            
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
            System.out.println("Base de données initialisée avec succès");
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation de la base de données: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion à la base de données fermée");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}