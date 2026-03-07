package com.gestionprojet.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Tache {
    private int id;
    private String titre;
    private String description;
    private Priorite priorite;
    private StatutTache statut;
    private LocalDateTime dateCreation;
    private LocalDate dateEcheance;
    private int projetId;
    private int assigneeId;
    private List<SousTache> sousTaches;

    public enum StatutTache {
        A_FAIRE("À faire"),
        EN_COURS("En cours"),
        TERMINE("Terminé"),
        EN_ATTENTE("En attente");

        private final String libelle;

        StatutTache(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public Tache() {
        this.priorite = Priorite.MOYENNE;
        this.statut = StatutTache.A_FAIRE;
        this.dateCreation = LocalDateTime.now();
        this.sousTaches = new ArrayList<>();
    }

    public Tache(String titre, String description, LocalDate dateEcheance, int projetId) {
        this();
        this.titre = titre;
        this.description = description;
        this.dateEcheance = dateEcheance;
        this.projetId = projetId;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Priorite getPriorite() { return priorite; }
    public void setPriorite(Priorite priorite) { this.priorite = priorite; }
    
    public StatutTache getStatut() { return statut; }
    public void setStatut(StatutTache statut) { this.statut = statut; }
    
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    
    public LocalDate getDateEcheance() { return dateEcheance; }
    public void setDateEcheance(LocalDate dateEcheance) { this.dateEcheance = dateEcheance; }
    
    public int getProjetId() { return projetId; }
    public void setProjetId(int projetId) { this.projetId = projetId; }
    
    public int getAssigneeId() { return assigneeId; }
    public void setAssigneeId(int assigneeId) { this.assigneeId = assigneeId; }
    
    public List<SousTache> getSousTaches() { return sousTaches; }
    public void setSousTaches(List<SousTache> sousTaches) { this.sousTaches = sousTaches; }
    
    public void addSousTache(SousTache sousTache) {
        sousTaches.add(sousTache);
    }
}