package com.taskmanager.controllers;

import com.taskmanager.models.Task;
import com.taskmanager.services.TaskService;
import com.taskmanager.utils.DateUtils;
import com.taskmanager.utils.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

public class TaskController {

    @FXML private TableView<Task> table;
    @FXML private TableColumn<Task, String> titleCol;
    @FXML private TableColumn<Task, String> priorityCol;
    @FXML private TextField searchField;
    @FXML private DatePicker filterDate;

    private final TaskService taskService = TaskService.getInstance();
    private final ObservableList<Task> tasks = FXCollections.observableArrayList();

    @FXML
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
    public void onAddTask() {
        // Show task form (not implemented here)
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
        List<Task> results = taskService.search(q, d);
        tasks.setAll(results);
    }
}