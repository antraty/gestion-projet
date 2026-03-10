package com.gestionprojet.controllers;

import com.gestionprojet.dao.ProjetDAO;
import com.gestionprojet.dao.SousTacheDAO;
import com.gestionprojet.dao.UtilisateurDAO;
import com.gestionprojet.models.*;
import com.gestionprojet.utils.DateUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DetailsTacheController {

    @FXML private Label titreLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label projetLabel;
    @FXML private Label prioriteLabel;
    @FXML private Label statutLabel;
    @FXML private Label echeanceLabel;
    @FXML private Label assigneeLabel;
    @FXML private Label dateCreationLabel;
    @FXML private VBox sousTachesContainer;
    @FXML private ProgressBar progressionBar;
    @FXML private Label progressionLabel;
    @FXML private Button fermerButton;

    private Tache tache;
    private ProjetDAO projetDAO = new ProjetDAO();
    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private SousTacheDAO sousTacheDAO = new SousTacheDAO();

    @FXML
    public void initialize() {
        // Initialisation
    }

    public void setTache(Tache tache) {
        this.tache = tache;
        chargerDetails();
        chargerSousTaches();
    }

    private void chargerDetails() {
        titreLabel.setText(tache.getTitre());
        descriptionLabel.setText(tache.getDescription() != null && !tache.getDescription().isEmpty() ? 
            tache.getDescription() : "Aucune description");
        
        // Charger le projet
        Projet projet = projetDAO.getById(tache.getProjetId());
        projetLabel.setText(projet != null ? projet.getNom() : "Projet inconnu");
        
        prioriteLabel.setText(tache.getPriorite().getLibelle());
        
        // Couleur selon la priorité
        switch (tache.getPriorite()) {
            case URGENTE:
                prioriteLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                break;
            case HAUTE:
                prioriteLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                break;
            case MOYENNE:
                prioriteLabel.setStyle("-fx-text-fill: blue;");
                break;
            default:
                prioriteLabel.setStyle("-fx-text-fill: green;");
        }
        
        statutLabel.setText(tache.getStatut().getLibelle());
        echeanceLabel.setText(tache.getDateEcheance() != null ? 
            DateUtils.formatDate(tache.getDateEcheance()) : "Non définie");
        
        // Charger l'utilisateur assigné
        if (tache.getAssigneeId() > 0) {
            Utilisateur assignee = utilisateurDAO.getById(tache.getAssigneeId());
            assigneeLabel.setText(assignee != null ? assignee.getNom() : "Non assigné");
        } else {
            assigneeLabel.setText("Non assigné");
        }
        
        dateCreationLabel.setText(DateUtils.formatDateTime(tache.getDateCreation()));
    }

    private void chargerSousTaches() {
        sousTachesContainer.getChildren().clear();
        
        java.util.List<SousTache> sousTaches = sousTacheDAO.getByTache(tache.getId());
        
        if (sousTaches.isEmpty()) {
            Label emptyLabel = new Label("Aucune sous-tâche");
            emptyLabel.setStyle("-fx-text-fill: #999;");
            sousTachesContainer.getChildren().add(emptyLabel);
            progressionBar.setProgress(0);
            progressionLabel.setText("0%");
            return;
        }
        
        int terminees = 0;
        
        for (SousTache sousTache : sousTaches) {
            CheckBox checkBox = new CheckBox(sousTache.getTitre());
            checkBox.setSelected(sousTache.isEstTerminee());
            
            if (sousTache.isEstTerminee()) {
                terminees++;
                checkBox.setStyle("-fx-text-fill: #999;");
            }
            
            final int sousTacheId = sousTache.getId();
            // Ajouter un listener pour mettre à jour le statut
            checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                sousTacheDAO.updateStatut(sousTacheId, newVal);
                if (newVal) {
                    checkBox.setStyle("-fx-text-fill: #999;");
                } else {
                    checkBox.setStyle("");
                }
                mettreAJourProgression();
            });
            
            sousTachesContainer.getChildren().add(checkBox);
        }
        
        // Mettre à jour la progression
        double progression = (terminees * 100.0) / sousTaches.size();
        progressionBar.setProgress(progression / 100);
        progressionLabel.setText(String.format("%.0f%%", progression));
    }

    private void mettreAJourProgression() {
        int total = sousTachesContainer.getChildren().size();
        if (total == 0) {
            progressionBar.setProgress(0);
            progressionLabel.setText("0%");
            return;
        }
        
        int terminees = 0;
        
        for (int i = 0; i < total; i++) {
            javafx.scene.Node node = sousTachesContainer.getChildren().get(i);
            if (node instanceof CheckBox) {
                CheckBox cb = (CheckBox) node;
                if (cb.isSelected()) {
                    terminees++;
                }
            }
        }
        
        double progression = (terminees * 100.0) / total;
        progressionBar.setProgress(progression / 100);
        progressionLabel.setText(String.format("%.0f%%", progression));
    }

    @FXML
    private void handleFermer() {
        ((Stage) fermerButton.getScene().getWindow()).close();
    }
}