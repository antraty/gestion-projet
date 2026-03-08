package com.gestionprojet.controllers;

import com.gestionprojet.GestionProjetApp;
import com.gestionprojet.dao.ProjetDAO;
import com.gestionprojet.dao.TacheDAO;
import com.gestionprojet.models.Projet;
import com.gestionprojet.models.Tache;
import com.gestionprojet.utils.SessionManager;
import com.gestionprojet.utils.DateUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.cell.PropertyValueFactory;

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

    // TableView et colonnes pour l'onglet "Toutes les tâches"
    @FXML private TableView<Tache> toutesTachesTableView;
    @FXML private TableColumn<Tache, String> titreColumnToutesTaches;
    @FXML private TableColumn<Tache, String> prioriteColumnToutesTaches;
    @FXML private TableColumn<Tache, String> statutColumnToutesTaches;
    @FXML private TableColumn<Tache, LocalDate> echeanceColumnToutesTaches;

    private ProjetDAO projetDAO = new ProjetDAO();
    private TacheDAO tacheDAO = new TacheDAO();

    @FXML
    public void initialize() {
        if (SessionManager.getInstance().estConnecte()) {
            welcomeLabel.setText("Bienvenue, " + SessionManager.getInstance().getUtilisateurConnecte().getNom());
            chargerDonnees();
            chargerToutesLesTachesAttribuees(); // Charger les tâches attribuées pour l'onglet "Toutes les tâches"
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
        
        // Récupérer les tâches assignées à l'utilisateur via la DAO
        List<Tache> tachesAttribuees = tacheDAO.getByAssignee(utilisateurId);
        
        toutesTachesTableView.setItems(FXCollections.observableArrayList(tachesAttribuees));
        
        // Configurer les colonnes
        titreColumnToutesTaches.setCellValueFactory(new PropertyValueFactory<>("titre"));
        prioriteColumnToutesTaches.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPriorite().getLibelle()));
        statutColumnToutesTaches.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatut().getLibelle()));
        echeanceColumnToutesTaches.setCellValueFactory(new PropertyValueFactory<>("dateEcheance"));
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