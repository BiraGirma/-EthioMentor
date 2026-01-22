package com.ethiomentor.service;

import com.ethiomentor.dao.UserDAO;
import com.ethiomentor.model.User;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Login using email or username as identifier
     * @param identifier email or username
     * @param password plaintext password
     * @return User object if authentication successful, null otherwise
     */
    public User login(String identifier, String password) {
        return userDAO.authenticate(identifier, password);
    }

    /**
     * Optional: Login with role restriction
     * @param email user's email
     * @param password plaintext password
     * @param role expected role (e.g., "student", "mentor", "admin")
     * @return User object if authentication successful and role matches, null otherwise
     */
    public User login(String email, String password, String role) {
        User user = userDAO.authenticate(email, password);
        if (user != null && user.getRole().equalsIgnoreCase(role)) {
            return user;
        }
        return null;
    }
}
