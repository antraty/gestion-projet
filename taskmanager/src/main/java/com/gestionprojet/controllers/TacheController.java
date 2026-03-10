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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TacheController {

    @FXML private TableView<Tache> tachesTableView;
    @FXML private TableColumn<Tache, String> titreColumn;
    @FXML private TableColumn<Tache, String> prioriteColumn;
    @FXML private TableColumn<Tache, String> statutColumn;
    @FXML private TableColumn<Tache, LocalDate> echeanceColumn;
    @FXML private TableColumn<Tache, Void> editColumn; // Nouvelle colonne pour l'édition
    @FXML private TableColumn<Tache, Void> deleteColumn; // Nouvelle colonne pour la suppression
    
    @FXML private ComboBox<String> filtreStatutCombo;
    @FXML private ComboBox<Priorite> filtrePrioriteCombo;
    @FXML private DatePicker filtreDatePicker;

    // Formulaire d'ajout uniquement
    @FXML private TextField ajoutTitreField;
    @FXML private TextArea ajoutDescriptionArea;
    @FXML private ComboBox<Priorite> ajoutPrioriteCombo;
    @FXML private ComboBox<Tache.StatutTache> ajoutStatutCombo;
    @FXML private DatePicker ajoutDateEcheancePicker;
    @FXML private ComboBox<Utilisateur> ajoutAssigneeCombo;
    @FXML private Button ajouterButton;

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
                    if (tache != null) {
                        switch (tache.getPriorite()) {
                            case URGENTE:
                                setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                                break;
                            case HAUTE:
                                setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                                break;
                            case MOYENNE:
                                setStyle("-fx-text-fill: blue;");
                                break;
                            default:
                                setStyle("-fx-text-fill: green;");
                        }
                    }
                }
            }
        });

        // Colonne d'édition avec bouton
        editColumn.setCellFactory(param -> new TableCell<Tache, Void>() {
            private final Button editButton = new Button("✏️ Modifier");
            
            {
                editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                editButton.setOnAction(event -> {
                    Tache tache = getTableView().getItems().get(getIndex());
                    ouvrirFenetreEdition(tache);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });

        // Colonne de suppression avec bouton
        deleteColumn.setCellFactory(param -> new TableCell<Tache, Void>() {
            private final Button deleteButton = new Button("🗑️ Supprimer");
            
            {
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Tache tache = getTableView().getItems().get(getIndex());
                    supprimerTache(tache);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
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

    private void ouvrirFenetreEdition(Tache tache) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gestionprojet/views/editerTache.fxml"));
            Parent root = loader.load();
            
            EditerTacheController controller = loader.getController();
            controller.setTache(tache);
            controller.setProjet(projetActuel);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier la tâche: " + tache.getTitre());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Rafraîchir la liste après modification
            chargerTaches();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre d'édition", Alert.AlertType.ERROR);
        }
    }

    private void supprimerTache(Tache tache) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer la tâche");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer la tâche \"" + tache.getTitre() + "\" ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (tacheDAO.delete(tache.getId())) {
                chargerTaches();
                showAlert("Succès", "Tâche supprimée avec succès", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erreur", "Impossible de supprimer la tâche", Alert.AlertType.ERROR);
            }
        }
    }

    private void setupComboBoxes() {
        // Filtres
        filtreStatutCombo.setItems(FXCollections.observableArrayList(
            "Tous", "À faire", "En cours", "Terminé", "En attente"
        ));
        filtreStatutCombo.setValue("Tous");

        filtrePrioriteCombo.setItems(FXCollections.observableArrayList(Priorite.values()));
        filtrePrioriteCombo.setValue(null);

        // Formulaire d'ajout
        ajoutPrioriteCombo.setItems(FXCollections.observableArrayList(Priorite.values()));
        ajoutPrioriteCombo.setValue(Priorite.MOYENNE);

        ajoutStatutCombo.setItems(FXCollections.observableArrayList(Tache.StatutTache.values()));
        ajoutStatutCombo.setValue(Tache.StatutTache.A_FAIRE);
    }

    private void chargerUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurDAO.getAll();
        ajoutAssigneeCombo.setItems(FXCollections.observableArrayList(utilisateurs));
        
        // Configuration de l'affichage des utilisateurs
        ajoutAssigneeCombo.setCellFactory(param -> new ListCell<Utilisateur>() {
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
        
        ajoutAssigneeCombo.setButtonCell(new ListCell<Utilisateur>() {
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
        if (!validateAjoutTache()) {
            return;
        }

        Tache nouvelleTache = new Tache(
            ajoutTitreField.getText(),
            ajoutDescriptionArea.getText(),
            ajoutDateEcheancePicker.getValue(),
            projetActuel.getId()
        );
        nouvelleTache.setPriorite(ajoutPrioriteCombo.getValue());
        nouvelleTache.setStatut(ajoutStatutCombo.getValue());
        if (ajoutAssigneeCombo.getValue() != null) {
            nouvelleTache.setAssigneeId(ajoutAssigneeCombo.getValue().getId());
        }

        if (tacheDAO.creer(nouvelleTache)) {
            clearAjoutForm();
            chargerTaches();
            showAlert("Succès", "Tâche créée avec succès", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Erreur", "Impossible de créer la tâche", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clearAjoutForm() {
        ajoutTitreField.clear();
        ajoutDescriptionArea.clear();
        ajoutPrioriteCombo.setValue(Priorite.MOYENNE);
        ajoutStatutCombo.setValue(Tache.StatutTache.A_FAIRE);
        ajoutDateEcheancePicker.setValue(null);
        ajoutAssigneeCombo.setValue(null);
    }

    private boolean validateAjoutTache() {
        if (ajoutTitreField.getText() == null || ajoutTitreField.getText().trim().isEmpty()) {
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
            
            // Rafraîchir après modification des sous-tâches
            chargerTaches();
            
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