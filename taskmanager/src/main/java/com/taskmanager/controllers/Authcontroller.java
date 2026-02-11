package com.taskmanager.controllers;

import com.taskmanager.exceptions.AuthException;
import com.taskmanager.models.User;
import com.taskmanager.services.AuthService;
import com.taskmanager.utils.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AuthController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMe;
    @FXML private Hyperlink registerLink;
    @FXML private Button loginButton;

    private final AuthService authService = AuthService.getInstance();

    @FXML
    public void initialize() {
        registerLink.setOnAction(e -> {
            try {
                MainController main = (MainController) registerLink.getScene().lookup("#rootPane").getUserData();
            } catch (Exception ignored) {}
            // Could call parent controller to swap view
        });

        loginButton.setOnAction(e -> onLogin());
    }

    private void onLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            User user = authService.login(email, password);
            AlertUtils.info("Succès", "Connecté en tant que " + user.getName());
            // TODO: switch to dashboard view
        } catch (AuthException ex) {
            AlertUtils.error("Erreur d'authentification", ex.getMessage());
        }
    }
}