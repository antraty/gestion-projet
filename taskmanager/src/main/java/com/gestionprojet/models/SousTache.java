package com.gestionprojet.models;

public class SousTache {
    private int id;
    private String titre;
    private boolean estTerminee;
    private int tacheId;

    public SousTache() {}

    public SousTache(String titre, int tacheId) {
        this.titre = titre;
        this.tacheId = tacheId;
        this.estTerminee = false;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    
    public boolean isEstTerminee() { return estTerminee; }
    public void setEstTerminee(boolean estTerminee) { this.estTerminee = estTerminee; }
    
    public int getTacheId() { return tacheId; }
    public void setTacheId(int tacheId) { this.tacheId = tacheId; }
}