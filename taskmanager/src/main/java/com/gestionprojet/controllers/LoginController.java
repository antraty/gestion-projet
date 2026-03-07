package com.gestionprojet.controllers;

import com.gestionprojet.GestionProjetApp;
import com.gestionprojet.dao.UtilisateurDAO;
import com.gestionprojet.models.Utilisateur;
import com.gestionprojet.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML private VBox loginForm;
    @FXML private VBox registerForm;
    @FXML private TextField loginEmailField;
    @FXML private PasswordField loginPasswordField;
    @FXML private TextField registerNomField;
    @FXML private TextField registerEmailField;
    @FXML private PasswordField registerPasswordField;
    @FXML private PasswordField registerConfirmPasswordField;
    @FXML private Label messageLabel;

    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    @FXML
    public void initialize() {
        showLoginForm();
    }

    @FXML
    private void showLoginForm() {
        loginForm.setVisible(true);
        loginForm.setManaged(true);
        registerForm.setVisible(false);
        registerForm.setManaged(false);
        messageLabel.setText("");
    }

    @FXML
    private void showRegisterForm() {
        loginForm.setVisible(false);
        loginForm.setManaged(false);
        registerForm.setVisible(true);
        registerForm.setManaged(true);
        messageLabel.setText("");
    }

    @FXML
    private void handleLogin() {
        String email = loginEmailField.getText();
        String password = loginPasswordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Veuillez remplir tous les champs", Alert.AlertType.WARNING);
            return;
        }

        Utilisateur utilisateur = utilisateurDAO.authentifier(email, password);
        if (utilisateur != null) {
            SessionManager.getInstance().setUtilisateurConnecte(utilisateur);
            try {
                GestionProjetApp.showDashboardView();
            } catch (Exception e) {
                e.printStackTrace();
                showMessage("Erreur lors du chargement du tableau de bord", Alert.AlertType.ERROR);
            }
        } else {
            showMessage("Email ou mot de passe incorrect", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRegister() {
        String nom = registerNomField.getText();
        String email = registerEmailField.getText();
        String password = registerPasswordField.getText();
        String confirmPassword = registerConfirmPasswordField.getText();

        if (nom.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showMessage("Veuillez remplir tous les champs", Alert.AlertType.WARNING);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showMessage("Les mots de passe ne correspondent pas", Alert.AlertType.ERROR);
            return;
        }

        Utilisateur utilisateur = new Utilisateur(nom, email, password);
        if (utilisateurDAO.inscrire(utilisateur)) {
            showMessage("Inscription réussie ! Vous pouvez maintenant vous connecter.", Alert.AlertType.INFORMATION);
            showLoginForm();
        } else {
            showMessage("Erreur lors de l'inscription. L'email est peut-être déjà utilisé.", Alert.AlertType.ERROR);
        }
    }

    private void showMessage(String message, Alert.AlertType type) {
        messageLabel.setText(message);
        messageLabel.setStyle(type == Alert.AlertType.ERROR ? "-fx-text-fill: red;" : 
                              type == Alert.AlertType.WARNING ? "-fx-text-fill: orange;" : 
                              "-fx-text-fill: green;");
    }
}