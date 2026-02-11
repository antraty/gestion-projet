package com.taskmanager.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class Task {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private LocalDate createdAt;
    private LocalDate dueDate;
    private final StringProperty priority = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private int assignedUserId;

    public Task() {}

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }

    public String getTitle() { return title.get(); }
    public void setTitle(String t) { this.title.set(t); }
    public StringProperty titleProperty() { return title; }

    public String getDescription() { return description.get(); }
    public void setDescription(String d) { this.description.set(d); }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getPriority() { return priority.get(); }
    public void setPriority(String p) { this.priority.set(p); }
    public StringProperty priorityProperty() { return priority; }

    public String getCategory() { return category.get(); }
    public void setCategory(String c) { this.category.set(c); }

    public String getStatus() { return status.get(); }
    public void setStatus(String s) { this.status.set(s); }

    public int getAssignedUserId() { return assignedUserId; }
    public void setAssignedUserId(int id) { this.assignedUserId = id; }
}