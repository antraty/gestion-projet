package com.taskmanager.models;

public enum TaskStatus {
    A_FAIRE("À faire"),
    EN_COURS("En cours"),
    TERMINE("Terminé");
    
    private final String displayName;
    
    TaskStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}