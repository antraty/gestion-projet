package com.taskmanager.controllers;

import com.taskmanager.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {
    
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckbox;
    @FXML private Label errorLabel;
    
    private AuthService authService;
    
    @FXML
    public void initialize() {
        authService = AuthService.getInstance();
        errorLabel.setVisible(false);
    }
    
    @FXML
    private void handleLogin() {
        try {
            // Validation des champs
            if (!validateFields()) {
                return;
            }
            
            String email = emailField.getText();
            String password = passwordField.getText();
            boolean rememberMe = rememberMeCheckbox.isSelected();
            
            System.out.println("Tentative de connexion pour: " + email);
            
            if (authService.login(email, password, rememberMe)) {
                System.out.println("Connexion réussie");
                // Connexion réussie, ouvrir le dashboard
                openDashboard();
            } else {
                showError("Email ou mot de passe incorrect");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de la connexion: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCreateAccount() {
        try {
            // Ouvrir la fenêtre d'inscription
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Créer un compte");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            
            // Fermer la fenêtre de connexion
            Stage loginStage = (Stage) emailField.getScene().getWindow();
            loginStage.close();
            
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de l'ouverture de la page d'inscription");
        }
    }
    
    private boolean validateFields() {
        if (emailField.getText() == null || emailField.getText().trim().isEmpty() || 
            passwordField.getText() == null || passwordField.getText().isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return false;
        }
        
        String email = emailField.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Format d'email invalide");
            return false;
        }
        
        return true;
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private void openDashboard() {
        try {
            System.out.println("Ouverture du dashboard...");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            
            // Charger le CSS
            try {
                String css = getClass().getResource("/styles/main.css").toExternalForm();
                scene.getStylesheets().add(css);
            } catch (Exception e) {
                System.out.println("CSS non trouvé, on continue sans");
            }
            
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setTitle("Tableau de bord - Gestionnaire de Tâches");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setWidth(1200);
            stage.setHeight(800);
            stage.centerOnScreen();
            
            System.out.println("Dashboard ouvert avec succès");
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement du dashboard: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur inattendue: " + e.getMessage());
        }
    }
}