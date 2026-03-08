package com.taskmanager.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainController {

    @FXML 
    private StackPane contentPane;

    @FXML
    public void initialize() {
        // Charge le dashboard par défaut au démarrage
        showDashboard();
    }

    @FXML
    private void showDashboard() {
        loadView("/views/dashboard.fxml");
    }

    @FXML
    private void showTasks() {
        loadView("/views/task-list.fxml");
    }

    // La méthode magique pour la fluidité
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
            // On remplace le contenu du StackPane
            // setAll() supprime l'ancienne vue et met la nouvelle proprement
            contentPane.getChildren().setAll(view);
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement : " + fxmlPath);
        }
    }
}