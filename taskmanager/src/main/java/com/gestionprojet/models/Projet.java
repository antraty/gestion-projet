package com.gestionprojet.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.gestionprojet.utils.DateUtils;

public class Projet {
    private int id;
    private String nom;
    private String description;
    private LocalDateTime dateCreation;
    private LocalDate dateEcheance;
    private int proprietaireId;
    private List<Tache> taches;

    public Projet() {
        this.taches = new ArrayList<>();
        this.dateCreation = LocalDateTime.now();
    }

    public Projet(String nom, String description, LocalDate dateEcheance, int proprietaireId) {
        this();
        this.nom = nom;
        this.description = description;
        this.dateEcheance = dateEcheance;
        this.proprietaireId = proprietaireId;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    
    public LocalDate getDateEcheance() { return dateEcheance; }
    public void setDateEcheance(LocalDate dateEcheance) { this.dateEcheance = dateEcheance; }
    
    public int getProprietaireId() { return proprietaireId; }
    public void setProprietaireId(int proprietaireId) { this.proprietaireId = proprietaireId; }
    
    public List<Tache> getTaches() { return taches; }
    public void setTaches(List<Tache> taches) { this.taches = taches; }
    
    public void addTache(Tache tache) {
        taches.add(tache);
    }
    @Override
    public String toString() {
    return nom + (dateEcheance != null ? " (Échéance: " + DateUtils.formatDate(dateEcheance) + ")" : "");
    }

}