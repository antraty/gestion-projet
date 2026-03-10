package com.gestionprojet.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    
    private static final int HASH_COST = 12;
    
    /**
    
     * @param password 
     * @return 
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(HASH_COST));
    }
    
    /**
     * @param password l
     * @param hashedPassword 
     * @return 
     */
    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}