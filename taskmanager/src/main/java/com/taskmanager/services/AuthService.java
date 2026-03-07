package com.taskmanager.services;

import com.taskmanager.models.User;
import com.taskmanager.utils.PasswordUtils;
import java.sql.*;

public class AuthService {
    private static AuthService instance;
    private User currentUser;
    private DatabaseService dbService;
    
    private AuthService() {
        dbService = DatabaseService.getInstance();
        if (dbService.getConnection() == null) {
            throw new RuntimeException("Connexion à la base de données impossible");
        }
    }
    
    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }
    
    public boolean login(String email, String password, boolean rememberMe) {
        String query = "SELECT * FROM users WHERE email = ?";
        
        try (PreparedStatement pstmt = dbService.getConnection().prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password_hash");
                    if (PasswordUtils.checkPassword(password, hashedPassword)) {
                        currentUser = new User();
                        currentUser.setId(rs.getInt("id"));
                        currentUser.setName(rs.getString("name"));
                        currentUser.setEmail(rs.getString("email"));
                        currentUser.setPasswordHash(hashedPassword);
                        
                        System.out.println("Utilisateur connecté: " + currentUser.getName());
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la connexion: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean register(String name, String email, String password) {
        if (isEmailExists(email)) {
            return false;
        }
        
        String query = "INSERT INTO users (name, email, password_hash) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = dbService.getConnection().prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, PasswordUtils.hashPassword(password));
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Inscription réussie pour: " + email);
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'inscription: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean isEmailExists(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (PreparedStatement pstmt = dbService.getConnection().prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la vérification d'email: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public void logout() {
        currentUser = null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}