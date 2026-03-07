package com.taskmanager.controllers;

import com.taskmanager.models.Task;
import com.taskmanager.models.TaskStatus;
import com.taskmanager.models.TaskPriority;
import com.taskmanager.models.TaskCategory;
import com.taskmanager.models.User;
import com.taskmanager.services.TaskService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;

public class TaskController {
    
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<TaskPriority> priorityCombo;
    @FXML private ComboBox<TaskCategory> categoryCombo;
    @FXML private ComboBox<TaskStatus> statusCombo;
    @FXML private Label errorLabel;
    
    private TaskService taskService;
    private User currentUser;
    private DashboardController dashboardController;
    private Task currentTask;
    private boolean isEditMode = false;
    
    @FXML
    public void initialize() {
        taskService = TaskService.getInstance();
        errorLabel.setVisible(false);
        
        // Initialiser les combobox
        priorityCombo.setItems(javafx.collections.FXCollections.observableArrayList(
            TaskPriority.values()
        ));
        categoryCombo.setItems(javafx.collections.FXCollections.observableArrayList(
            TaskCategory.values()
        ));
        statusCombo.setItems(javafx.collections.FXCollections.observableArrayList(
            TaskStatus.values()
        ));
        
        // Valeurs par défaut
        priorityCombo.setValue(TaskPriority.MOYENNE);
        categoryCombo.setValue(TaskCategory.PERSONNEL);
        statusCombo.setValue(TaskStatus.A_FAIRE);
        dueDatePicker.setValue(LocalDate.now().plusDays(1));
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }
    
    public void setTask(Task task) {
        this.currentTask = task;
        this.isEditMode = true;
        
        // Remplir les champs avec les données de la tâche
        titleField.setText(task.getTitle());
        descriptionArea.setText(task.getDescription());
        dueDatePicker.setValue(task.getDueDate());
        priorityCombo.setValue(task.getPriority());
        categoryCombo.setValue(task.getCategory());
        statusCombo.setValue(task.getStatus());
    }
    
    @FXML
    public void handleSave() {
        if (!validateFields()) {
            return;
        }
        
        if (isEditMode) {
            updateTask();
        } else {
            createTask();
        }
    }
    
    public void createTask() {
        Task task = new Task(
            titleField.getText(),
            descriptionArea.getText(),
            dueDatePicker.getValue(),
            priorityCombo.getValue(),
            categoryCombo.getValue(),
            currentUser.getId()
        );
        
        if (taskService.createTask(task)) {
            closeDialog();
            if (dashboardController != null) {
                dashboardController.loadTasks();
                dashboardController.updateDashboardStats();
            }
        } else {
            showError("Erreur lors de la création de la tâche");
        }
    }
    
    private void updateTask() {
        currentTask.setTitle(titleField.getText());
        currentTask.setDescription(descriptionArea.getText());
        currentTask.setDueDate(dueDatePicker.getValue());
        currentTask.setPriority(priorityCombo.getValue());
        currentTask.setCategory(categoryCombo.getValue());
        currentTask.setStatus(statusCombo.getValue());
        
        if (taskService.updateTask(currentTask)) {
            closeDialog();
            if (dashboardController != null) {
                dashboardController.loadTasks();
                dashboardController.updateDashboardStats();
            }
        } else {
            showError("Erreur lors de la mise à jour de la tâche");
        }
    }
    
    private boolean validateFields() {
        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            showError("Le titre est obligatoire");
            return false;
        }
        
        if (titleField.getText().length() > 255) {
            showError("Le titre ne doit pas dépasser 255 caractères");
            return false;
        }
        
        if (dueDatePicker.getValue() == null) {
            showError("Veuillez sélectionner une date d'échéance");
            return false;
        }
        
        LocalDate today = LocalDate.now();
        if (dueDatePicker.getValue().isBefore(today)) {
            showError("La date d'échéance doit être aujourd'hui ou à l'avenir");
            return false;
        }
        
        return true;
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private void closeDialog() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
}