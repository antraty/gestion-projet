package com.taskmanager.controllers;

import com.taskmanager.services.ReportService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML private Label totalTasksLabel;
    @FXML private Label overdueLabel;
    @FXML private Label todayLabel;
    @FXML private Label highPriorityLabel;

    private final ReportService reportService = ReportService.getInstance();

    @FXML
    public void initialize() {
        totalTasksLabel.setText(String.valueOf(reportService.totalTasks()));
        overdueLabel.setText(String.valueOf(reportService.overdueTasks()));
        todayLabel.setText(String.valueOf(reportService.tasksForToday()));
        highPriorityLabel.setText(String.valueOf(reportService.highPriorityTasks()));
    }
}