package com.taskmanager.controllers;

import com.taskmanager.models.Task;
import com.taskmanager.services.TaskService;
import com.taskmanager.utils.DateUtils;
import com.taskmanager.utils.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class TaskController {
    @FXML
    private void onResetFilters() {
        // 1. Vide le champ texte
        searchField.clear();
        
        // 2. Remet la date à null
        filterDate.setValue(null);
        
        // 3. RÉINITIALISATION PROPRE DES SELECTS
        // Utiliser clearSelection() force le retour du "Prompt Text"
        priorityFilterCombo.getSelectionModel().clearSelection();
        categoryFilterCombo.getSelectionModel().clearSelection();
        statusFilterCombo.getSelectionModel().clearSelection();
        
        // 4. On relance la recherche pour afficher toutes les tâches
        onSearch();
        
        // 5. Redonne le focus au champ de recherche pour le confort
        searchField.requestFocus();
    }

    @FXML private TableView<Task> table;
    @FXML private TableColumn<Task, String> titleCol;
    @FXML private TableColumn<Task, String> priorityCol;
    @FXML private TextField searchField;
    @FXML private DatePicker filterDate; 
    @FXML private ComboBox<String> priorityFilterCombo;
    @FXML private ComboBox<String> categoryFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;


    private final TaskService taskService = TaskService.getInstance();
    private final ObservableList<Task> tasks = FXCollections.observableArrayList();

    public void initialize() {
        titleCol.setCellValueFactory(c -> c.getValue().titleProperty());
        priorityCol.setCellValueFactory(c -> c.getValue().priorityProperty());

        table.setItems(tasks);
        loadTasks();
    }

    public void loadTasks() {
        List<Task> all = taskService.findAll();
        tasks.setAll(all);
    }

    @FXML
    private void onAddTask() {
        try {
            // 1. Charger le FXML du formulaire
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/task-form.fxml"));
            Parent root = loader.load();
            
            // 2. Récupérer le contrôleur du formulaire pour extraire les données après
            TaskFormController formController = loader.getController();
            
            // 3. Créer et afficher la fenêtre (Stage)
            Stage stage = new Stage();
            stage.setTitle("Nouvelle Tâche");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // 4. Récupérer l'objet Task créé
            Task taskCreee = formController.getTask();
            
            if (taskCreee != null) {
                // Règle de gestion : on définit la date de création maintenant
                taskCreee.setCreatedAt(LocalDate.now());
                // On lui assigne l'utilisateur actuel (à adapter selon ta gestion de session)
                taskCreee.setAssignedUserId(1); 

                // 5. ENVOI VERS LA BASE VIA LE SERVICE
                TaskService.getInstance().create(taskCreee);
                
                // 6. Rafraîchir ton affichage (TableView)
                loadTasks(); 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onEditTask() {
        Task selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.warn("Sélection", "Aucune tâche sélectionnée");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/task-form.fxml"));
            Parent root = loader.load();
            TaskFormController formController = loader.getController();

            // Pré-remplir le formulaire
            formController.setTask(selected);

            Stage stage = new Stage();
            stage.setTitle("Modifier la tâche");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            Task updatedTask = formController.getTask();
            if (updatedTask != null) {
                taskService.update(updatedTask);
                loadTasks();
            }
        } catch (Exception ex) {
            AlertUtils.error("Erreur", "Impossible d'ouvrir le formulaire");
            ex.printStackTrace();
        }
    }

    @FXML
    public void onDeleteTask() {
        Task sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertUtils.warn("Sélection", "Aucune tâche sélectionnée");
            return;
        }
        boolean ok = AlertUtils.confirm("Suppression", "Supprimer la tâche " + sel.getTitle() + " ?");
        if (ok) {
            taskService.delete(sel.getId());
            loadTasks();
        }
    }

    @FXML
    public void onSearch() {
        String q = searchField.getText();
        LocalDate d = filterDate.getValue();
        String priority = priorityFilterCombo.getValue();
        String category = categoryFilterCombo.getValue();
        String status = statusFilterCombo.getValue();

        List<Task> results = taskService.search(q, d, priority, category, status);
        tasks.setAll(results);
    }
}