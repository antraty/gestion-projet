package com.taskmanager.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {
    private int id;
    private String title;
    private String description;
    private LocalDateTime creationDate;
    private LocalDate dueDate;
    private TaskPriority priority;
    private TaskCategory category;
    private TaskStatus status;
    private int userId;
    
    public Task() {
        this.creationDate = LocalDateTime.now();
        this.status = TaskStatus.A_FAIRE;
    }
    
    public Task(String title, String description, LocalDate dueDate, 
                TaskPriority priority, TaskCategory category, int userId) {
        this.title = title;
        this.description = description;
        this.creationDate = LocalDateTime.now();
        this.dueDate = dueDate;
        this.priority = priority;
        this.category = category;
        this.status = TaskStatus.A_FAIRE;
        this.userId = userId;
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    
    public TaskCategory getCategory() { return category; }
    public void setCategory(TaskCategory category) { this.category = category; }
    
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}