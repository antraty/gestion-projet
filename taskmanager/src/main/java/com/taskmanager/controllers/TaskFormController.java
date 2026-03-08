package com.taskmanager.controllers;

import com.taskmanager.models.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class TaskFormController {
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> statusCombo;


    private Task task; // Pour modification, sinon null pour création
    private boolean validated = false; // Pour savoir si l'utilisateur a validé

    // Appelée par TaskController pour pré-remplir le formulaire (modification)
    public void setTask(Task t) {
        this.task = t;
        titleField.setText(t.getTitle());
        descriptionField.setText(t.getDescription());
        dueDatePicker.setValue(t.getDueDate());
        priorityCombo.setValue(t.getPriority());
        categoryCombo.setValue(task.getCategory()); 
        statusCombo.setValue(task.getStatus()); 
    }

    // Appelée par TaskController pour récupérer la tâche créée ou modifiée
    public Task getTask() {
        if (!validated) return null; // Si l'utilisateur a annulé
        if (task == null) task = new Task();
        task.setTitle(titleField.getText());
        task.setDescription(descriptionField.getText());
        task.setDueDate(dueDatePicker.getValue());
        task.setPriority(priorityCombo.getValue());
        task.setCategory(categoryCombo.getValue()); 
        task.setStatus(statusCombo.getValue() != null ? statusCombo.getValue() : "À faire");
        return task;
    }

    // Méthode liée au bouton "Valider" du formulaire (à ajouter dans le FXML)
    @FXML
    private void onValidate() {
        // Petite validation de peer : on n'accepte pas de tâche sans titre
        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champ vide");
            alert.setHeaderText(null);
            alert.setContentText("Le titre de la tâche est obligatoire !");
            alert.showAndWait();
            return;
        }
        if (categoryCombo.getValue() == null || categoryCombo.getValue().trim().isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Champ vide");
        alert.setHeaderText(null);
        alert.setContentText("La catégorie est obligatoire !");
        alert.showAndWait();
        return;
        }
        
        validated = true;
        titleField.getScene().getWindow().hide();
    }

    // Méthode liée au bouton "Annuler" (à ajouter dans le FXML)
    @FXML
    private void onCancel() {
        validated = false;
        titleField.getScene().getWindow().hide();
    }
}