package com.taskmanager.services;

import com.taskmanager.dao.UserDAO;
import com.taskmanager.exceptions.AuthException;
import com.taskmanager.models.User;
import com.taskmanager.utils.PasswordHasher;

public class AuthService {
    private static AuthService instance;
    private final UserDAO userDAO = new UserDAO();

    private AuthService() {}

    public static AuthService getInstance() {
        if (instance == null) instance = new AuthService();
        return instance;
    }

    public User register(String name, String email, String password) throws AuthException {
        if (userDAO.findByEmail(email) != null) {
            throw new AuthException("Email déjà utilisé");
        }
        String hash = PasswordHasher.hash(password);
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPasswordHash(hash);
        int id = userDAO.create(u);
        u.setId(id);
        // Stocker l'utilisateur dans la session après inscription
        com.taskmanager.models.Session.getInstance().setCurrentUser(u);
        return u;
    }

    public User login(String email, String password) throws AuthException {
        User u = userDAO.findByEmail(email);
        if (u == null) throw new AuthException("Utilisateur non trouvé");
        if (!PasswordHasher.verify(password, u.getPasswordHash())) throw new AuthException("Mot de passe incorrect");
        // Stocker l'utilisateur dans la session
        com.taskmanager.models.Session.getInstance().setCurrentUser(u);
        return u;
    }
}