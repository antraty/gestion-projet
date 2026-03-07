package com.gestionprojet.controllers;

import com.gestionprojet.dao.ProjetDAO;
import com.gestionprojet.models.Projet;
import com.gestionprojet.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;

public class ProjetController {

    @FXML private TextField nomField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker dateEcheancePicker;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private ProjetDAO projetDAO = new ProjetDAO();
    private Projet projetActuel;
    private boolean modeCreation = true;

    @FXML
    public void initialize() {
        if (!modeCreation && projetActuel != null) {
            chargerProjet();
        }
    }

    public void setModeCreation(boolean mode) {
        this.modeCreation = mode;
        if (!mode) {
            saveButton.setText("Mettre à jour");
        }
    }

    public void setProjet(Projet projet) {
        this.projetActuel = projet;
        this.modeCreation = false;
        chargerProjet();
    }

    private void chargerProjet() {
        if (projetActuel != null) {
            nomField.setText(projetActuel.getNom());
            descriptionArea.setText(projetActuel.getDescription());
            dateEcheancePicker.setValue(projetActuel.getDateEcheance());
        }
    }

    @FXML
    private void handleSave() {
        if (!validateInputs()) {
            return;
        }

        if (modeCreation) {
            Projet nouveauProjet = new Projet(
                nomField.getText(),
                descriptionArea.getText(),
                dateEcheancePicker.getValue(),
                SessionManager.getInstance().getUtilisateurConnecte().getId()
            );

            if (projetDAO.creer(nouveauProjet)) {
                showAlert("Succès", "Projet créé avec succès", Alert.AlertType.INFORMATION);
                fermerFenetre();
            } else {
                showAlert("Erreur", "Impossible de créer le projet", Alert.AlertType.ERROR);
            }
        } else {
            projetActuel.setNom(nomField.getText());
            projetActuel.setDescription(descriptionArea.getText());
            projetActuel.setDateEcheance(dateEcheancePicker.getValue());

            if (projetDAO.update(projetActuel)) {
                showAlert("Succès", "Projet mis à jour avec succès", Alert.AlertType.INFORMATION);
                fermerFenetre();
            } else {
                showAlert("Erreur", "Impossible de mettre à jour le projet", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleCancel() {
        fermerFenetre();
    }

    private boolean validateInputs() {
        if (nomField.getText() == null || nomField.getText().trim().isEmpty()) {
            showAlert("Validation", "Le nom du projet est requis", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void fermerFenetre() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}