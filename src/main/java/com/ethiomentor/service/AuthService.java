package com.ethiomentor.service;

import com.ethiomentor.dao.UserDAO;
import com.ethiomentor.model.User;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Login using email or username as identifier.
     * @return the user (without password hash) if authentication succeeded, null otherwise
     */
    public User login(String identifier, String password) {
        return userDAO.authenticate(identifier, password);
    }

    /**
     * Login that additionally requires the account to have the given role.
     * @return the user if authentication succeeded and the role matches, null otherwise
     */
    public User login(String identifier, String password, String role) {
        User user = userDAO.authenticate(identifier, password);
        if (user != null && user.getRole().equalsIgnoreCase(role)) {
            return user;
        }
        return null;
    }
}
