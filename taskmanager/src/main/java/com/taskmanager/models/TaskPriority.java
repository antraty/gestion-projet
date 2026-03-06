package com.taskmanager.models;

public enum TaskPriority {
    BASSE("Basse"),
    MOYENNE("Moyenne"),
    HAUTE("Haute"),
    URGENT("Urgent");
    
    private final String displayName;
    
    TaskPriority(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}