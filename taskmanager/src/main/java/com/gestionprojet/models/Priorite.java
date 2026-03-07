package com.gestionprojet.models;

public enum Priorite {
    BASSE("Basse"),
    MOYENNE("Moyenne"),
    HAUTE("Haute"),
    URGENTE("Urgente");

    private final String libelle;

    Priorite(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    public static Priorite fromLibelle(String libelle) {
        for (Priorite p : Priorite.values()) {
            if (p.libelle.equals(libelle)) {
                return p;
            }
        }
        return MOYENNE;
    }
}