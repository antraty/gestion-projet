package com.taskmanager.controllers;

import com.taskmanager.models.Session;
import com.taskmanager.models.User;
import com.taskmanager.services.ReportService;
import com.taskmanager.utils.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML private Label totalTasksLabel;
    @FXML private Label overdueLabel;
    @FXML private Label todayLabel;
    @FXML private Label highPriorityLabel;

    private final ReportService reportService = ReportService.getInstance();

    @FXML
    public void initialize() {
        User current = Session.getInstance().getCurrentUser();
        if(current == null){
            // Afficher un message ou rediriger vers la page de login
            Alert alert = new Alert(Alert.AlertType.ERROR, "Vous devez être connecté pour accéder à cette page");
            alert.showAndWait();

            // Redirection vers login
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/views/login.fxml"));
                javafx.scene.Parent root = loader.load();
                javafx.stage.Stage stage = (javafx.stage.Stage)totalTasksLabel.getScene().getWindow();
                stage.setScene(new javafx.scene.Scene(root));
                stage.show();
            }catch (Exception e){
                AlertUtils.error("Erreur critique", "Impossible de charger la page de connexion");
                // loguer l'erreur pour le debug
                e.printStackTrace();
            }
        }
        totalTasksLabel.setText(String.valueOf(reportService.totalTasks()));
        overdueLabel.setText(String.valueOf(reportService.overdueTasks()));
        todayLabel.setText(String.valueOf(reportService.tasksForToday()));
        highPriorityLabel.setText(String.valueOf(reportService.highPriorityTasks()));
    }
}