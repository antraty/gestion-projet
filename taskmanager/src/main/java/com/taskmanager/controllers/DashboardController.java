package com.taskmanager.controllers;

import com.taskmanager.models.*;
import com.taskmanager.services.AuthService;
import com.taskmanager.services.TaskService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class DashboardController {
    
    @FXML private Label welcomeLabel;
    @FXML private Label totalTasksLabel;
    @FXML private Label overdueTasksLabel;
    @FXML private Label todayTasksLabel;
    @FXML private Label highPriorityLabel;
    @FXML private PieChart statusPieChart;
    
    @FXML private TextField searchField;
    @FXML private ComboBox<TaskStatus> statusFilter;
    @FXML private ComboBox<TaskPriority> priorityFilter;
    @FXML private ComboBox<TaskCategory> categoryFilter;
    @FXML private DatePicker dueDateFilter;
    @FXML private Button clearFiltersButton;
    
    @FXML private ToggleButton tableViewToggle;
    @FXML private ToggleButton cardViewToggle;
    @FXML private StackPane viewContainer;
    
    private TableView<Task> taskTableView;
    private FlowPane cardPane;
    private AuthService authService;
    private TaskService taskService;
    private ObservableList<Task> tasks;
    private User currentUser;
    
    @FXML
    public void initialize() {
        authService = AuthService.getInstance();
        taskService = TaskService.getInstance();
        currentUser = authService.getCurrentUser();
        
        if (currentUser == null) {
            handleLogout();
            return;
        }
        
        welcomeLabel.setText("Bienvenue, " + currentUser.getName() + " !");
        
        initializeFilters();
        initializeTableView();
        initializeCardView();
        setupViewToggle();
        
        loadTasks();
        updateDashboardStats();
    }
    
    private void initializeFilters() {
        statusFilter.setItems(FXCollections.observableArrayList(TaskStatus.values()));
        priorityFilter.setItems(FXCollections.observableArrayList(TaskPriority.values()));
        categoryFilter.setItems(FXCollections.observableArrayList(TaskCategory.values()));
        
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        priorityFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        categoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        dueDateFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }
    
    private void initializeTableView() {
        taskTableView = new TableView<>();
        taskTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Task, String> titleCol = new TableColumn<>("Titre");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(150);
        
        TableColumn<Task, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(200);
        
        TableColumn<Task, LocalDate> dueDateCol = new TableColumn<>("Date échéance");
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        dueDateCol.setPrefWidth(120);
        dueDateCol.setCellFactory(column -> new TableCell<Task, LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) setText(null);
                else setText(formatter.format(date));
            }
        });
        
        TableColumn<Task, TaskPriority> priorityCol = new TableColumn<>("Priorité");
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        priorityCol.setPrefWidth(80);
        priorityCol.setCellFactory(column -> new TableCell<Task, TaskPriority>() {
            @Override
            protected void updateItem(TaskPriority priority, boolean empty) {
                super.updateItem(priority, empty);
                if (empty || priority == null) {
                    setText(null);
                } else {
                    setText(priority.getDisplayName());
                }
            }
        });
        
        TableColumn<Task, TaskCategory> categoryCol = new TableColumn<>("Catégorie");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(100);
        categoryCol.setCellFactory(column -> new TableCell<Task, TaskCategory>() {
            @Override
            protected void updateItem(TaskCategory category, boolean empty) {
                super.updateItem(category, empty);
                if (empty || category == null) {
                    setText(null);
                } else {
                    setText(category.getDisplayName());
                }
            }
        });
        
        TableColumn<Task, TaskStatus> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        statusCol.setCellFactory(column -> new TableCell<Task, TaskStatus>() {
            @Override
            protected void updateItem(TaskStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                } else {
                    setText(status.getDisplayName());
                }
            }
        });
        
        TableColumn<Task, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(param -> new TableCell<Task, Void>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox pane = new HBox(5, editButton, deleteButton);
            
            {
                editButton.setOnAction(event -> {
                    Task task = getTableView().getItems().get(getIndex());
                    handleEditTask(task);
                });
                
                deleteButton.setOnAction(event -> {
                    Task task = getTableView().getItems().get(getIndex());
                    handleDeleteTask(task);
                });
                
                editButton.getStyleClass().add("button-small");
                deleteButton.getStyleClass().add("button-small");
                pane.setPadding(new Insets(5));
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
        
        taskTableView.getColumns().addAll(titleCol, descCol, dueDateCol, priorityCol, categoryCol, statusCol, actionsCol);
        
        taskTableView.setRowFactory(tv -> new TableRow<Task>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (task == null || empty) {
                    setStyle("");
                } else {
                    switch (task.getPriority()) {
                        case URGENT:
                            setStyle("-fx-background-color: #ffebee;");
                            break;
                        case HAUTE:
                            setStyle("-fx-background-color: #fff3e0;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
    }
    
    private void initializeCardView() {
        cardPane = new FlowPane();
        cardPane.setHgap(10);
        cardPane.setVgap(10);
        cardPane.setPadding(new Insets(10));
        cardPane.setPrefWrapLength(800);
    }
    
    private void setupViewToggle() {
        ToggleGroup viewToggleGroup = new ToggleGroup();
        tableViewToggle.setToggleGroup(viewToggleGroup);
        cardViewToggle.setToggleGroup(viewToggleGroup);
        tableViewToggle.setSelected(true);
        
        viewToggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == tableViewToggle) {
                showTableView();
            } else {
                showCardView();
            }
        });
        
        showTableView();
    }
    
    private void showTableView() {
        viewContainer.getChildren().clear();
        viewContainer.getChildren().add(taskTableView);
    }
    
    private void showCardView() {
        viewContainer.getChildren().clear();
        updateCardView();
        ScrollPane scrollPane = new ScrollPane(cardPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        viewContainer.getChildren().add(scrollPane);
    }
    
    private void updateCardView() {
        cardPane.getChildren().clear();
        if (tasks != null) {
            for (Task task : tasks) {
                cardPane.getChildren().add(createTaskCard(task));
            }
        }
    }
    
    private VBox createTaskCard(Task task) {
        VBox card = new VBox(10);
        card.getStyleClass().add("task-card");
        card.setPrefWidth(250);
        card.setPrefHeight(200);
        card.setPadding(new Insets(10));
        
        Label titleLabel = new Label(task.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-wrap-text: true;");
        
        Label descLabel = new Label(task.getDescription());
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(60);
        descLabel.setStyle("-fx-text-fill: #666;");
        
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(5);
        infoGrid.setVgap(5);
        
        infoGrid.add(new Label("Échéance:"), 0, 0);
        infoGrid.add(new Label(task.getDueDate() != null ? 
            task.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Non définie"), 1, 0);
        
        infoGrid.add(new Label("Priorité:"), 0, 1);
        Label priorityLabel = new Label(task.getPriority().getDisplayName());
        switch (task.getPriority()) {
            case URGENT:
                priorityLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
                break;
            case HAUTE:
                priorityLabel.setStyle("-fx-text-fill: #ef6c00;");
                break;
            case MOYENNE:
                priorityLabel.setStyle("-fx-text-fill: #2196F3;");
                break;
            default:
                priorityLabel.setStyle("-fx-text-fill: #666;");
        }
        infoGrid.add(priorityLabel, 1, 1);
        
        infoGrid.add(new Label("Statut:"), 0, 2);
        Label statusLabel = new Label(task.getStatus().getDisplayName());
        infoGrid.add(statusLabel, 1, 2);
        
        HBox buttonBox = new HBox(10);
        Button editBtn = new Button("Modifier");
        Button deleteBtn = new Button("Supprimer");
        editBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 5 10;");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 5 10;");
        
        editBtn.setOnAction(e -> handleEditTask(task));
        deleteBtn.setOnAction(e -> handleDeleteTask(task));
        
        buttonBox.getChildren().addAll(editBtn, deleteBtn);
        
        card.getChildren().addAll(titleLabel, new Separator(), descLabel, infoGrid, buttonBox);
        
        return card;
    }
    
    public void loadTasks() {
        try {
            List<Task> loadedTasks = taskService.getUserTasks(currentUser.getId());
            if (loadedTasks == null) {
                loadedTasks = FXCollections.observableArrayList();
            }
            tasks = FXCollections.observableArrayList(loadedTasks);
            taskTableView.setItems(tasks);
            updateCardView();
        } catch (Exception e) {
            showError("Erreur lors du chargement des tâches: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void applyFilters() {
        try {
            List<Task> filteredTasks = taskService.getFilteredTasks(
                currentUser.getId(),
                searchField.getText() != null ? searchField.getText().trim() : "",
                statusFilter.getValue(),
                priorityFilter.getValue(),
                categoryFilter.getValue(),
                dueDateFilter.getValue()
            );
            
            if (filteredTasks == null) {
                filteredTasks = FXCollections.observableArrayList();
            }
            
            tasks.setAll(filteredTasks);
            updateCardView();
            updateDashboardStats();
        } catch (Exception e) {
            showError("Erreur lors de l'application des filtres: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void clearFilters() {
        searchField.clear();
        statusFilter.setValue(null);
        priorityFilter.setValue(null);
        categoryFilter.setValue(null);
        dueDateFilter.setValue(null);
    }
    
    @FXML
    private void handleAddTask() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/task-form.fxml"));
            DialogPane dialogPane = loader.load();
            
            TaskController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            controller.setDashboardController(this);
            
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Nouvelle tâche");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(welcomeLabel.getScene().getWindow());
            
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                controller.handleSave();
            }
            
        } catch (IOException e) {
            showError("Erreur lors de l'ouverture du formulaire: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleEditTask(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/task-form.fxml"));
            DialogPane dialogPane = loader.load();
            
            TaskController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            controller.setDashboardController(this);
            controller.setTask(task);
            
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Modifier la tâche");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(welcomeLabel.getScene().getWindow());
            
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                controller.handleSave();
            }
            
        } catch (IOException e) {
            showError("Erreur lors de l'ouverture du formulaire: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleDeleteTask(Task task) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la tâche");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette tâche ?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (taskService.deleteTask(task.getId(), currentUser.getId())) {
                        loadTasks();
                        updateDashboardStats();
                    } else {
                        showError("Erreur lors de la suppression de la tâche");
                    }
                } catch (Exception e) {
                    showError("Erreur lors de la suppression: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    
    public void updateDashboardStats() {
        try {
            totalTasksLabel.setText(String.valueOf(taskService.getTotalTasks(currentUser.getId())));
            overdueTasksLabel.setText(String.valueOf(taskService.getOverdueTasks(currentUser.getId())));
            todayTasksLabel.setText(String.valueOf(taskService.getTasksForToday(currentUser.getId())));
            highPriorityLabel.setText(String.valueOf(taskService.getHighPriorityTasks(currentUser.getId())));
            
            updatePieChart();
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour des statistiques: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updatePieChart() {
        List<Integer> counts = taskService.getTasksCountByStatus(currentUser.getId());
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        TaskStatus[] statuses = TaskStatus.values();
        for (int i = 0; i < statuses.length; i++) {
            if (counts.get(i) > 0) {
                pieChartData.add(new PieChart.Data(
                    statuses[i].getDisplayName() + " (" + counts.get(i) + ")", 
                    counts.get(i)
                ));
            }
        }
        
        statusPieChart.setData(pieChartData);
        statusPieChart.setTitle("Répartition des tâches");
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Une erreur s'est produite");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML
    private void handleLogout() {
        authService.logout();
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());
            
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setTitle("Connexion - Gestionnaire de Tâches");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}