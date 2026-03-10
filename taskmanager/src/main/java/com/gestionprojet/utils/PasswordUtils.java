package com.gestionprojet.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    
    private static final int HASH_COST = 12;
    
    /**
     * Hache un mot de passe en clair
     * @param password le mot de passe en clair
     * @return le mot de passe haché
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(HASH_COST));
    }
    
    /**
     * @param password le mot de passe en clair à vérifier
     * @param hashedPassword le mot de passe haché stocké
     * @return true si correspond, false sinon
     */
    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}