package com.gestionprojet.utils;

import com.gestionprojet.models.Utilisateur;

public class SessionManager {
    private static SessionManager instance;
    private Utilisateur utilisateurConnecte;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setUtilisateurConnecte(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
    }

    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public void deconnexion() {
        this.utilisateurConnecte = null;
    }

    public boolean estConnecte() {
        return utilisateurConnecte != null;
    }
}