package com.gestionprojet.controllers;

import com.gestionprojet.dao.SousTacheDAO;
import com.gestionprojet.dao.TacheDAO;
import com.gestionprojet.dao.UtilisateurDAO;
import com.gestionprojet.models.*;
import com.gestionprojet.utils.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class TacheController {

    @FXML private TableView<Tache> tachesTableView;
    @FXML private TableColumn<Tache, String> titreColumn;
    @FXML private TableColumn<Tache, String> prioriteColumn;
    @FXML private TableColumn<Tache, String> statutColumn;
    @FXML private TableColumn<Tache, LocalDate> echeanceColumn;
    @FXML private ComboBox<String> filtreStatutCombo;
    @FXML private ComboBox<Priorite> filtrePrioriteCombo;
    @FXML private DatePicker filtreDatePicker;

    @FXML private TextField titreField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<Priorite> prioriteCombo;
    @FXML private ComboBox<Tache.StatutTache> statutCombo;
    @FXML private DatePicker dateEcheancePicker;
    @FXML private ComboBox<Utilisateur> assigneeCombo;

    private TacheDAO tacheDAO = new TacheDAO();
    private SousTacheDAO sousTacheDAO = new SousTacheDAO();
    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private Projet projetActuel;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupComboBoxes();
        chargerUtilisateurs();
    }

    public void setProjet(Projet projet) {
        this.projetActuel = projet;
        chargerTaches();
    }

    private void setupTableColumns() {
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        prioriteColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPriorite().getLibelle()));
        statutColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatut().getLibelle()));
        echeanceColumn.setCellValueFactory(new PropertyValueFactory<>("dateEcheance"));

        // Style pour les priorités
        prioriteColumn.setCellFactory(column -> new TableCell<Tache, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    Tache tache = getTableView().getItems().get(getIndex());
                    switch (tache.getPriorite()) {
                        case URGENTE:
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            break;
                        case HAUTE:
                            setStyle("-fx-text-fill: orange;");
                            break;
                        case MOYENNE:
                            setStyle("-fx-text-fill: blue;");
                            break;
                        default:
                            setStyle("-fx-text-fill: green;");
                    }
                }
            }
        });

        // Double-clic pour voir les détails
        tachesTableView.setRowFactory(tv -> {
            TableRow<Tache> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Tache tache = row.getItem();
                    afficherSousTaches(tache);
                }
            });
            return row;
        });
    }

    private void setupComboBoxes() {
        // Filtres
        filtreStatutCombo.setItems(FXCollections.observableArrayList(
            "Tous", "À faire", "En cours", "Terminé", "En attente"
        ));
        filtreStatutCombo.setValue("Tous");

        filtrePrioriteCombo.setItems(FXCollections.observableArrayList(Priorite.values()));
        filtrePrioriteCombo.setValue(null);

        // Formulaire
        prioriteCombo.setItems(FXCollections.observableArrayList(Priorite.values()));
        prioriteCombo.setValue(Priorite.MOYENNE);

        statutCombo.setItems(FXCollections.observableArrayList(Tache.StatutTache.values()));
        statutCombo.setValue(Tache.StatutTache.A_FAIRE);
    }

    private void chargerUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurDAO.getAll();
        assigneeCombo.setItems(FXCollections.observableArrayList(utilisateurs));
    }

    private void chargerTaches() {
        if (projetActuel != null) {
            List<Tache> taches = tacheDAO.getByProjet(projetActuel.getId());
            tachesTableView.setItems(FXCollections.observableArrayList(taches));
            appliquerFiltres();
        }
    }

    @FXML
    private void appliquerFiltres() {
        if (tachesTableView.getItems() == null) return;

        List<Tache> tachesFiltrees = tachesTableView.getItems().stream()
            .filter(t -> filterByStatut(t))
            .filter(t -> filterByPriorite(t))
            .filter(t -> filterByDate(t))
            .toList();

        tachesTableView.setItems(FXCollections.observableArrayList(tachesFiltrees));
    }

    private boolean filterByStatut(Tache tache) {
        String filtre = filtreStatutCombo.getValue();
        if ("Tous".equals(filtre)) return true;
        return tache.getStatut().getLibelle().equals(filtre);
    }

    private boolean filterByPriorite(Tache tache) {
        Priorite filtre = filtrePrioriteCombo.getValue();
        if (filtre == null) return true;
        return tache.getPriorite() == filtre;
    }

    private boolean filterByDate(Tache tache) {
        LocalDate filtre = filtreDatePicker.getValue();
        if (filtre == null) return true;
        return filtre.equals(tache.getDateEcheance());
    }

    @FXML
    private void reinitialiserFiltres() {
        filtreStatutCombo.setValue("Tous");
        filtrePrioriteCombo.setValue(null);
        filtreDatePicker.setValue(null);
        chargerTaches();
    }

    @FXML
    private void handleAjouterTache() {
        if (!validateTacheInput()) {
            return;
        }

        Tache nouvelleTache = new Tache(
            titreField.getText(),
            descriptionArea.getText(),
            dateEcheancePicker.getValue(),
            projetActuel.getId()
        );
        nouvelleTache.setPriorite(prioriteCombo.getValue());
        nouvelleTache.setStatut(statutCombo.getValue());
        if (assigneeCombo.getValue() != null) {
            nouvelleTache.setAssigneeId(assigneeCombo.getValue().getId());
        }

        if (tacheDAO.creer(nouvelleTache)) {
            clearForm();
            chargerTaches();
            showAlert("Succès", "Tâche créée avec succès", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Erreur", "Impossible de créer la tâche", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleModifierTache() {
        Tache tacheSelectionnee = tachesTableView.getSelectionModel().getSelectedItem();
        if (tacheSelectionnee == null) {
            showAlert("Sélection", "Veuillez sélectionner une tâche à modifier", Alert.AlertType.WARNING);
            return;
        }

        // Remplir le formulaire avec les données de la tâche
        titreField.setText(tacheSelectionnee.getTitre());
        descriptionArea.setText(tacheSelectionnee.getDescription());
        prioriteCombo.setValue(tacheSelectionnee.getPriorite());
        statutCombo.setValue(tacheSelectionnee.getStatut());
        dateEcheancePicker.setValue(tacheSelectionnee.getDateEcheance());
        
        // Trouver l'utilisateur assigné
        if (tacheSelectionnee.getAssigneeId() > 0) {
            Utilisateur assignee = utilisateurDAO.getById(tacheSelectionnee.getAssigneeId());
            assigneeCombo.setValue(assignee);
        }
    }

    @FXML
    private void handleSupprimerTache() {
        Tache tacheSelectionnee = tachesTableView.getSelectionModel().getSelectedItem();
        if (tacheSelectionnee == null) {
            showAlert("Sélection", "Veuillez sélectionner une tâche à supprimer", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer la tâche");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette tâche ?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            if (tacheDAO.delete(tacheSelectionnee.getId())) {
                chargerTaches();
                showAlert("Succès", "Tâche supprimée avec succès", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erreur", "Impossible de supprimer la tâche", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleMettreAJour() {
        Tache tacheSelectionnee = tachesTableView.getSelectionModel().getSelectedItem();
        if (tacheSelectionnee == null) {
            showAlert("Erreur", "Aucune tâche sélectionnée", Alert.AlertType.ERROR);
            return;
        }

        tacheSelectionnee.setTitre(titreField.getText());
        tacheSelectionnee.setDescription(descriptionArea.getText());
        tacheSelectionnee.setPriorite(prioriteCombo.getValue());
        tacheSelectionnee.setStatut(statutCombo.getValue());
        tacheSelectionnee.setDateEcheance(dateEcheancePicker.getValue());
        if (assigneeCombo.getValue() != null) {
            tacheSelectionnee.setAssigneeId(assigneeCombo.getValue().getId());
        }

        if (tacheDAO.update(tacheSelectionnee)) {
            clearForm();
            chargerTaches();
            showAlert("Succès", "Tâche mise à jour avec succès", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Erreur", "Impossible de mettre à jour la tâche", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clearForm() {
        titreField.clear();
        descriptionArea.clear();
        prioriteCombo.setValue(Priorite.MOYENNE);
        statutCombo.setValue(Tache.StatutTache.A_FAIRE);
        dateEcheancePicker.setValue(null);
        assigneeCombo.setValue(null);
    }

    private boolean validateTacheInput() {
        if (titreField.getText() == null || titreField.getText().trim().isEmpty()) {
            showAlert("Validation", "Le titre de la tâche est requis", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void afficherSousTaches(Tache tache) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gestionprojet/views/sousTache.fxml"));
            Parent root = loader.load();
            
            SousTacheController controller = loader.getController();
            controller.setTache(tache);
            
            Stage stage = new Stage();
            stage.setTitle("Sous-tâches: " + tache.getTitre());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre des sous-tâches", Alert.AlertType.ERROR);
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