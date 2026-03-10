package com.gestionprojet.controllers;

import com.gestionprojet.GestionProjetApp;
import com.gestionprojet.dao.ProjetDAO;
import com.gestionprojet.dao.TacheDAO;
import com.gestionprojet.models.Projet;
import com.gestionprojet.models.Tache;
import com.gestionprojet.utils.SessionManager;
import com.gestionprojet.utils.DateUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
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

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private ListView<Projet> projetsListView;
    @FXML private ListView<Tache> tachesAujourdhuiListView;
    @FXML private ListView<Tache> tachesSemaineListView;
    @FXML private ListView<Tache> tachesPlusTardListView;
    @FXML private Label projetCountLabel;
    @FXML private Label tacheCountLabel;

    @FXML private TableView<TacheDAO.TacheAvecProjet> toutesTachesTableView;
    @FXML private TableColumn<TacheDAO.TacheAvecProjet, String> titreColumnToutesTaches;
    @FXML private TableColumn<TacheDAO.TacheAvecProjet, String> projetColumnToutesTaches;
    @FXML private TableColumn<TacheDAO.TacheAvecProjet, String> prioriteColumnToutesTaches;
    @FXML private TableColumn<TacheDAO.TacheAvecProjet, String> statutColumnToutesTaches;
    @FXML private TableColumn<TacheDAO.TacheAvecProjet, LocalDate> echeanceColumnToutesTaches;

    private ProjetDAO projetDAO = new ProjetDAO();
    private TacheDAO tacheDAO = new TacheDAO();

    @FXML
    public void initialize() {
        if (SessionManager.getInstance().estConnecte()) {
            welcomeLabel.setText("Bienvenue, " + SessionManager.getInstance().getUtilisateurConnecte().getNom());
            chargerDonnees();
            chargerToutesLesTachesAttribuees();
        }
    }

    private void chargerDonnees() {
        int utilisateurId = SessionManager.getInstance().getUtilisateurConnecte().getId();
        
        // Charger les projets
        List<Projet> projets = projetDAO.getByProprietaire(utilisateurId);
        projetsListView.getItems().setAll(projets);
        projetCountLabel.setText(String.valueOf(projets.size()));

        // Configurer l'affichage des projets
        projetsListView.setCellFactory(lv -> new ListCell<Projet>() {
            @Override
            protected void updateItem(Projet projet, boolean empty) {
                super.updateItem(projet, empty);
                if (empty || projet == null) {
                    setText(null);
                } else {
                    setText(projet.getNom() + " (Échéance: " + 
                           (projet.getDateEcheance() != null ? DateUtils.formatDate(projet.getDateEcheance()) : "Non définie") + ")");
                }
            }
        });

        // Charger les tâches par catégorie
        chargerTachesParCategorie(utilisateurId);
        
        // Configurer l'affichage des tâches avec double-clic
        configurerAffichageTaches();
    }

    private void configurerAffichageTaches() {
        // Configuration pour tachesAujourdhuiListView
        tachesAujourdhuiListView.setCellFactory(lv -> new ListCell<Tache>() {
            @Override
            protected void updateItem(Tache tache, boolean empty) {
                super.updateItem(tache, empty);
                if (empty || tache == null) {
                    setText(null);
                } else {
                    setText(tache.getTitre() + " (" + tache.getPriorite().getLibelle() + ")");
                }
            }
        });
        
        // Double-clic sur une tâche du jour
        tachesAujourdhuiListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Tache tache = tachesAujourdhuiListView.getSelectionModel().getSelectedItem();
                if (tache != null) {
                    afficherDetailsTache(tache);
                }
            }
        });

        // Configuration pour tachesSemaineListView
        tachesSemaineListView.setCellFactory(lv -> new ListCell<Tache>() {
            @Override
            protected void updateItem(Tache tache, boolean empty) {
                super.updateItem(tache, empty);
                if (empty || tache == null) {
                    setText(null);
                } else {
                    setText(tache.getTitre() + " (" + tache.getPriorite().getLibelle() + ")");
                }
            }
        });
        
        // Double-clic sur une tâche de la semaine
        tachesSemaineListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Tache tache = tachesSemaineListView.getSelectionModel().getSelectedItem();
                if (tache != null) {
                    afficherDetailsTache(tache);
                }
            }
        });

        // Configuration pour tachesPlusTardListView
        tachesPlusTardListView.setCellFactory(lv -> new ListCell<Tache>() {
            @Override
            protected void updateItem(Tache tache, boolean empty) {
                super.updateItem(tache, empty);
                if (empty || tache == null) {
                    setText(null);
                } else {
                    setText(tache.getTitre() + " (" + tache.getPriorite().getLibelle() + ")");
                }
            }
        });
        
        // Double-clic sur une tâche plus tard
        tachesPlusTardListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Tache tache = tachesPlusTardListView.getSelectionModel().getSelectedItem();
                if (tache != null) {
                    afficherDetailsTache(tache);
                }
            }
        });
    }

    private void chargerTachesParCategorie(int utilisateurId) {
        // Tâches d'aujourd'hui
        List<Tache> tachesAujourdhui = tacheDAO.getTachesPourAujourdhui(utilisateurId);
        tachesAujourdhuiListView.getItems().setAll(tachesAujourdhui);
        
        // Tâches de la semaine
        List<Tache> tachesSemaine = tacheDAO.getTachesCetteSemaine(utilisateurId);
        tachesSemaineListView.getItems().setAll(tachesSemaine);
        
        // Tâches plus tard (non terminées, échéance future)
        List<Tache> toutesTaches = tacheDAO.getByAssignee(utilisateurId);
        List<Tache> tachesPlusTard = toutesTaches.stream()
            .filter(t -> t.getStatut() != Tache.StatutTache.TERMINE)
            .filter(t -> t.getDateEcheance() != null && t.getDateEcheance().isAfter(LocalDate.now().plusWeeks(1)))
            .toList();
        tachesPlusTardListView.getItems().setAll(tachesPlusTard);

        tacheCountLabel.setText(String.valueOf(toutesTaches.size()));
    }

    private void chargerToutesLesTachesAttribuees() {
        int utilisateurId = SessionManager.getInstance().getUtilisateurConnecte().getId();
        
        // Récupérer les tâches avec le nom du projet
        List<TacheDAO.TacheAvecProjet> tachesAttribuees = tacheDAO.getTachesAvecProjetByAssignee(utilisateurId);
        
        toutesTachesTableView.setItems(FXCollections.observableArrayList(tachesAttribuees));
        
        // Configurer les colonnes
        titreColumnToutesTaches.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTache().getTitre()));
        
        projetColumnToutesTaches.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomProjet()));
        
        prioriteColumnToutesTaches.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTache().getPriorite().getLibelle()));
        
        statutColumnToutesTaches.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTache().getStatut().getLibelle()));
        
        echeanceColumnToutesTaches.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getTache().getDateEcheance()));
        
        // Style pour les priorités
        prioriteColumnToutesTaches.setCellFactory(column -> new TableCell<TacheDAO.TacheAvecProjet, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    TacheDAO.TacheAvecProjet tacheAvecProjet = getTableView().getItems().get(getIndex());
                    if (tacheAvecProjet != null) {
                        switch (tacheAvecProjet.getTache().getPriorite()) {
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
        
        // Ajouter un double-clic pour voir les détails de la tâche
        toutesTachesTableView.setRowFactory(tv -> {
            TableRow<TacheDAO.TacheAvecProjet> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    TacheDAO.TacheAvecProjet tacheAvecProjet = row.getItem();
                    afficherDetailsTache(tacheAvecProjet.getTache());
                }
            });
            return row;
        });
    }

    private void afficherDetailsTache(Tache tache) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gestionprojet/views/detailsTache.fxml"));
            Parent root = loader.load();
            
            DetailsTacheController controller = loader.getController();
            controller.setTache(tache);
            
            Stage stage = new Stage();
            stage.setTitle("Détails de la tâche: " + tache.getTitre());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir les détails de la tâche");
        }
    }

    @FXML
    private void handleNouveauProjet() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gestionprojet/views/projet.fxml"));
            Parent root = loader.load();
            
            ProjetController controller = loader.getController();
            controller.setModeCreation(true);
            
            Stage stage = new Stage();
            stage.setTitle("Nouveau Projet");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Rafraîchir la liste des projets
            chargerDonnees();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de création de projet");
        }
    }

    @FXML
    private void handleProjetSelectionne() {
        Projet projetSelectionne = projetsListView.getSelectionModel().getSelectedItem();
        if (projetSelectionne != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gestionprojet/views/tache.fxml"));
                Parent root = loader.load();
                
                TacheController controller = loader.getController();
                controller.setProjet(projetSelectionne);
                
                Stage stage = new Stage();
                stage.setTitle("Tâches du projet: " + projetSelectionne.getNom());
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                
                // Rafraîchir les données
                chargerDonnees();
                chargerToutesLesTachesAttribuees();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible d'ouvrir la fenêtre des tâches");
            }
        }
    }

    @FXML
    private void handleDeconnexion() {
        SessionManager.getInstance().deconnexion();
        try {
            GestionProjetApp.showLoginView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}