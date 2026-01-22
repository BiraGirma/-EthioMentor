package com.ethiomentor.service;

import com.ethiomentor.dao.UserDAO;
import com.ethiomentor.model.User;
import com.ethiomentor.util.PasswordUtil;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Registers a new user.
     * @param user User object with username, fullName, email, password, role, university
     * @return true if registration succeeds, false if username/email exists or error
     */
    public boolean registerUser(User user) {
        try {
            // Check for existing email or username
            if (userDAO.findByEmail(user.getEmail()) != null) return false;
            if (userDAO.findByUsername(user.getUsername()) != null) return false;

            // Hash password
            user.setPassword(PasswordUtil.hash(user.getPassword()));

            // Normalize role to lowercase (must match DB check constraint)
            String role = user.getRole().toUpperCase().trim();

            if ("MENTEE".equals(role)) {
                user.setRole("student");   // 👈 MUST match DB
            } else if ("MENTOR".equals(role)) {
                user.setRole("mentor");
            } else {
                user.setRole("student");   // safe default
            }

            // Save user to database
            userDAO.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Authenticates user by email + role
     * @param email user's email
     * @param password plaintext password
     * @param role user's role (student / mentor / admin)
     * @return User object if login successful, null otherwise
     */
    public User login(String email, String password, String role) {
        try {
            // normalize role to lowercase
            role = role.toLowerCase().trim();
            return userDAO.authenticate(email, password, role);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get user by username
     */
    public User getUserByUsername(String username) {
        try {
            return userDAO.findByUsername(username);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        try {
            return userDAO.findByEmail(email);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Update user information. Hashes password if provided.
     */
    public boolean updateUser(User user) {
        try {
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(PasswordUtil.hash(user.getPassword()));
            }
            // Normalize role if provided
            if (user.getRole() != null && !user.getRole().isEmpty()) {
                user.setRole(user.getRole().toLowerCase().trim());
            }
            userDAO.update(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
