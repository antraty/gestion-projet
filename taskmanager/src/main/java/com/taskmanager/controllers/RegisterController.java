package com.taskmanager.controllers;

import com.taskmanager.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {
    
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    
    private AuthService authService;
    
    @FXML
    public void initialize() {
        authService = AuthService.getInstance();
        errorLabel.setVisible(false);
    }
    
    @FXML
    private void handleRegister() {
        if (!validateFields()) {
            return;
        }
        
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        
        if (authService.register(name, email, password)) {
            // Inscription réussie, retour à la connexion
            showSuccess("Compte créé avec succès !");
            handleBackToLogin();
        } else {
            showError("Cet email est déjà utilisé");
        }
    }
    
    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());
            
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setTitle("Connexion - Gestionnaire de Tâches");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean validateFields() {
        // Vérifier champs vides
        if (nameField.getText().isEmpty() || emailField.getText().isEmpty() || 
            passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
            showError("Tous les champs sont obligatoires");
            return false;
        }
        
        // Valider email
        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Format d'email invalide");
            return false;
        }
        
        // Valider mot de passe (minimum 6 caractères)
        if (passwordField.getText().length() < 6) {
            showError("Le mot de passe doit contenir au moins 6 caractères");
            return false;
        }
        
        // Vérifier correspondance des mots de passe
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("Les mots de passe ne correspondent pas");
            return false;
        }
        
        return true;
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(true);
    }
    
    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: green;");
        errorLabel.setVisible(true);
    }
}