package com.gestionprojet.controllers;

import com.gestionprojet.dao.SousTacheDAO;
import com.gestionprojet.models.SousTache;
import com.gestionprojet.models.Tache;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

public class SousTacheController {

    @FXML private TableView<SousTache> sousTachesTableView;
    @FXML private TableColumn<SousTache, Boolean> termineeColumn;
    @FXML private TableColumn<SousTache, String> titreColumn;
    @FXML private TextField nouveauTitreField;

    private SousTacheDAO sousTacheDAO = new SousTacheDAO();
    private Tache tacheActuelle;

    @FXML
    public void initialize() {
        setupTableColumns();
    }

    public void setTache(Tache tache) {
        this.tacheActuelle = tache;
        chargerSousTaches();
    }

    private void setupTableColumns() {
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        
        termineeColumn.setCellValueFactory(new PropertyValueFactory<>("estTerminee"));
        termineeColumn.setCellFactory(CheckBoxTableCell.forTableColumn(termineeColumn));
        termineeColumn.setEditable(true);
        
        sousTachesTableView.setEditable(true);
    }

    private void chargerSousTaches() {
        if (tacheActuelle != null) {
            sousTachesTableView.setItems(
                FXCollections.observableArrayList(
                    sousTacheDAO.getByTache(tacheActuelle.getId())
                )
            );
        }
    }

    @FXML
    private void handleAjouterSousTache() {
        if (nouveauTitreField.getText() == null || nouveauTitreField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Veuillez saisir un titre pour la sous-tâche", Alert.AlertType.WARNING);
            return;
        }

        SousTache nouvelleSousTache = new SousTache(
            nouveauTitreField.getText().trim(),
            tacheActuelle.getId()
        );

        if (sousTacheDAO.creer(nouvelleSousTache)) {
            nouveauTitreField.clear();
            chargerSousTaches();
        } else {
            showAlert("Erreur", "Impossible de créer la sous-tâche", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSupprimerSousTache() {
        SousTache sousTacheSelectionnee = sousTachesTableView.getSelectionModel().getSelectedItem();
        if (sousTacheSelectionnee == null) {
            showAlert("Erreur", "Veuillez sélectionner une sous-tâche à supprimer", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer la sous-tâche");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette sous-tâche ?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            if (sousTacheDAO.delete(sousTacheSelectionnee.getId())) {
                chargerSousTaches();
            } else {
                showAlert("Erreur", "Impossible de supprimer la sous-tâche", Alert.AlertType.ERROR);
            }
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}