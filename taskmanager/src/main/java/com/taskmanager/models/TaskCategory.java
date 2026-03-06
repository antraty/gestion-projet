package com.taskmanager.models;

public enum TaskCategory {
    TRAVAIL("Travail"),
    PERSONNEL("Personnel"),
    ETUDES("Études");
    
    private final String displayName;
    
    TaskCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}