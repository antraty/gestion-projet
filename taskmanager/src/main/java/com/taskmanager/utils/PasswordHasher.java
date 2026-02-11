package com.taskmanager.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public static boolean verify(String password, String hash) {
        if (hash == null) return false;
        return BCrypt.checkpw(password, hash);
    }
}