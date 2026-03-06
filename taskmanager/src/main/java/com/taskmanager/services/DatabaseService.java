package com.taskmanager.services;

import com.taskmanager.config.DatabaseConfig;
import com.taskmanager.exceptions.DatabaseException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

public class DatabaseService {
    private static DatabaseService instance;
    private Connection conn;

    private DatabaseService() {}

    public static DatabaseService getInstance() {
        if (instance == null) instance = new DatabaseService();
        return instance;
    }

    public void initialize() {
        try {
            String user = com.taskmanager.config.AppConfig.getOrDefault("db.user", "root");
            String password = com.taskmanager.config.AppConfig.getOrDefault("db.password", "");
            conn = DriverManager.getConnection(DatabaseConfig.getJdbcUrl(), user, password);
            // Run schema if not exists
            try (InputStream in = getClass().getResourceAsStream("/../sql/schema.sql")) {
                // resource path adjusted; fallback to /sql/schema.sql
                InputStream schema = (in == null) ? getClass().getResourceAsStream("/sql/schema.sql") : in;
                if (schema != null) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(schema))) {
                        String sql;
                        StringBuilder sb = new StringBuilder();
                        while ((sql = br.readLine()) != null) {
                            sb.append(sql).append("\n");
                        }
                        Statement st = conn.createStatement();
                        for (String s : sb.toString().split(";")) {
                            String t = s.trim();
                            if (!t.isEmpty()) st.execute(t);
                        }
                    }
                }
            } catch (Exception ignored) {}

        } catch (SQLException e) {
            throw new DatabaseException("Can't open DB connection", e);
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public void close() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException ignored) {}
    }
}