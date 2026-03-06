package com.taskmanager.controllers;

import com.taskmanager.exceptions.AuthException;
import com.taskmanager.models.User;
import com.taskmanager.services.AuthService;
import com.taskmanager.utils.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AuthController {

    // Login
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMe;
    @FXML private Button loginButton;
    @FXML private Hyperlink registerLink;

    // Register
    @FXML private TextField nomField;
    @FXML private TextField emailRegisterField;
    @FXML private PasswordField passwordRegisterField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button registerButton;

    private final AuthService authService = AuthService.getInstance();

    public void initialize() {
        // Éléments présents uniquement sur LOGIN.FXML
        if (registerLink != null) {
            registerLink.setOnAction(e -> chargerVue("/views/register.fxml"));
        }
        
        if (loginButton != null) {
            loginButton.setOnAction(e -> onLogin());
        }

        // Éléments présents uniquement sur REGISTER.FXML
        if (registerButton != null) {
            registerButton.setOnAction(e -> register());
        }
    }

    // Petite méthode utilitaire pour simplifier la navigation et éviter les erreurs
    private void chargerVue(String cheminFxml) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(cheminFxml));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) loginButton.getScene().getWindow(); // ou registerLink
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception ex) {
            ex.printStackTrace();
            AlertUtils.error("Erreur", "Impossible de charger la vue : " + cheminFxml);
        }
    }

    private void onLogin() {
        // Récuperer les variables des champs de la connexion
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            User user = authService.login(email, password);
            AlertUtils.info("Succès", "Connecté en tant que " + user.getName());
            
            // Navigation vers le dashboard
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) loginButton.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();

        } catch (AuthException ex) {
            AlertUtils.error("Erreur d'authentification", ex.getMessage());
        } catch (Exception ex){
            ex.printStackTrace(); // <--- AJOUTEZ CETTE LIGNE
            AlertUtils.error("Erreur", "Impossible de charger le dashboard.");
        }
    }

    private void register(){
        // Récuperer les variables des champs de l'inscription
        String nom = nomField.getText();
        String email = emailRegisterField.getText();
        String password = passwordRegisterField.getText();
        String confirm = confirmPasswordField.getText();

        if(!password.equals(confirm)){
            AlertUtils.error("Erreur", "Les mots de passe ne correspondent pas");
            return;
        }

        try {
            User user = authService.register(nom, email, password);
            AlertUtils.info("Succès", "Compte créer pour " + user.getName());

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) registerButton.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();

        } catch(AuthException ex) {
            AlertUtils.error("Erreur d'inscription", ex.getMessage());
        } catch(Exception ex){
            AlertUtils.error(" Erreur","Impossible de charger le dashboard");
        }

    }
}