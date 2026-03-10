package com.gestionprojet.controllers;

import com.gestionprojet.dao.TacheDAO;
import com.gestionprojet.dao.UtilisateurDAO;
import com.gestionprojet.models.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

public class EditerTacheController {

    @FXML private Label titreFenetreLabel;
    @FXML private TextField titreField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<Priorite> prioriteCombo;
    @FXML private ComboBox<Tache.StatutTache> statutCombo;
    @FXML private DatePicker dateEcheancePicker;
    @FXML private ComboBox<Utilisateur> assigneeCombo;
    @FXML private Button sauvegarderButton;
    @FXML private Button annulerButton;

    private TacheDAO tacheDAO = new TacheDAO();
    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private Tache tacheActuelle;
    private Projet projetActuel;

    @FXML
    public void initialize() {
        setupCombos();
        chargerUtilisateurs();
    }

    private void setupCombos() {
        prioriteCombo.setItems(FXCollections.observableArrayList(Priorite.values()));
        statutCombo.setItems(FXCollections.observableArrayList(Tache.StatutTache.values()));
    }

    private void chargerUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurDAO.getAll();
        assigneeCombo.setItems(FXCollections.observableArrayList(utilisateurs));
        
        assigneeCombo.setCellFactory(param -> new ListCell<Utilisateur>() {
            @Override
            protected void updateItem(Utilisateur utilisateur, boolean empty) {
                super.updateItem(utilisateur, empty);
                if (empty || utilisateur == null) {
                    setText(null);
                } else {
                    setText(utilisateur.getNom());
                }
            }
        });
        
        assigneeCombo.setButtonCell(new ListCell<Utilisateur>() {
            @Override
            protected void updateItem(Utilisateur utilisateur, boolean empty) {
                super.updateItem(utilisateur, empty);
                if (empty || utilisateur == null) {
                    setText(null);
                } else {
                    setText(utilisateur.getNom());
                }
            }
        });
    }

    public void setTache(Tache tache) {
        this.tacheActuelle = tache;
        titreFenetreLabel.setText("Modifier la tâche: " + tache.getTitre());
        chargerTache();
    }

    public void setProjet(Projet projet) {
        this.projetActuel = projet;
    }

    private void chargerTache() {
        if (tacheActuelle != null) {
            titreField.setText(tacheActuelle.getTitre());
            descriptionArea.setText(tacheActuelle.getDescription());
            prioriteCombo.setValue(tacheActuelle.getPriorite());
            statutCombo.setValue(tacheActuelle.getStatut());
            dateEcheancePicker.setValue(tacheActuelle.getDateEcheance());
            
            if (tacheActuelle.getAssigneeId() > 0) {
                Utilisateur assignee = utilisateurDAO.getById(tacheActuelle.getAssigneeId());
                assigneeCombo.setValue(assignee);
            }
        }
    }

    @FXML
    private void handleSauvegarder() {
        if (!validateInputs()) {
            return;
        }

        tacheActuelle.setTitre(titreField.getText());
        tacheActuelle.setDescription(descriptionArea.getText());
        tacheActuelle.setPriorite(prioriteCombo.getValue());
        tacheActuelle.setStatut(statutCombo.getValue());
        tacheActuelle.setDateEcheance(dateEcheancePicker.getValue());
        
        if (assigneeCombo.getValue() != null) {
            tacheActuelle.setAssigneeId(assigneeCombo.getValue().getId());
        } else {
            tacheActuelle.setAssigneeId(0);
        }

        if (tacheDAO.update(tacheActuelle)) {
            showAlert("Succès", "Tâche mise à jour avec succès", Alert.AlertType.INFORMATION);
            fermerFenetre();
        } else {
            showAlert("Erreur", "Impossible de mettre à jour la tâche", Alert.AlertType.ERROR);
        }
    }

    private boolean validateInputs() {
        if (titreField.getText() == null || titreField.getText().trim().isEmpty()) {
            showAlert("Validation", "Le titre de la tâche est requis", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    @FXML
    private void handleAnnuler() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        ((Stage) annulerButton.getScene().getWindow()).close();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}