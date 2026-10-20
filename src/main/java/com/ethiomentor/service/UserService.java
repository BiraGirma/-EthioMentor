package com.ethiomentor.service;

import com.ethiomentor.dao.UserDAO;
import com.ethiomentor.model.User;
import com.ethiomentor.util.PasswordUtil;
import com.ethiomentor.util.InputUtil;

import java.sql.SQLException;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Validates the registration fields.
     *
     * @return null if everything is acceptable, otherwise a message that is safe to show to the user
     */
    public static String validateRegistration(User user, String plainPassword) {
        if (!InputUtil.isValidUsername(user.getUsername()))
            return "Username must be 3-30 characters: letters, digits, dot, dash or underscore.";
        if (InputUtil.isBlank(user.getFullName()) || user.getFullName().length() > 100)
            return "Please enter your full name (max 100 characters).";
        if (!InputUtil.isValidEmail(user.getEmail()))
            return "Please enter a valid email address.";
        if (!InputUtil.isValidPassword(plainPassword))
            return "Password must be 8-128 characters.";
        if (InputUtil.isBlank(user.getUniversity()) || user.getUniversity().length() > 100)
            return "Please choose your institution.";
        return null;
    }

    /** "java, ,  sql" -> "java,sql"; null/blank -> null; result is capped at 255 characters. */
    public static String cleanExpertise(String raw) {
        if (raw == null) return null;
        StringBuilder sb = new StringBuilder();
        for (String part : raw.split(",")) {
            String p = part.trim();
            if (p.isEmpty()) continue;
            if (sb.length() > 0) sb.append(',');
            sb.append(p);
        }
        if (sb.length() == 0) return null;
        return sb.length() > 255 ? sb.substring(0, 255) : sb.toString();
    }

    /**
     * Registers a new user after validating the input.
     * Only "mentor" and "student" can be self-registered; anything else becomes student.
     *
     * @param user      username, fullName, email, university, role and the PLAIN password
     * @param expertise optional comma-separated expertise (mentors only)
     * @return null on success, otherwise a message that is safe to show to the user
     */
    public String registerUser(User user, String expertise) {
        String plain = user.getPassword();
        String problem = validateRegistration(user, plain);
        if (problem != null) return problem;

        try {
            if (userDAO.findByEmail(user.getEmail()) != null || userDAO.findByUsername(user.getUsername()) != null)
                return "That username or email is already registered.";

            String role = user.getRole() == null ? "" : user.getRole().toUpperCase().trim();
            user.setRole("MENTOR".equals(role) ? "mentor" : "student");

            user.setPassword(PasswordUtil.hash(plain));
            userDAO.save(user, "mentor".equals(user.getRole()) ? cleanExpertise(expertise) : null);
            user.setPassword(null);
            return null;

        } catch (SQLException e) {
            user.setPassword(null);
            if ("23505".equals(e.getSQLState())) return "That username or email is already registered.";
            e.printStackTrace();
            return "Registration failed. Please try again later.";
        } catch (Exception e) {
            user.setPassword(null);
            e.printStackTrace();
            return "Registration failed. Please try again later.";
        }
    }

    public User getUserById(int id) {
        try {
            return userDAO.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User getUserByUsername(String username) {
        try {
            return userDAO.findByUsername(username);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User getUserByEmail(String email) {
        try {
            return userDAO.findByEmail(email);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Updates the profile of the logged-in user. Null arguments mean "leave unchanged".
     * On success the given User object is updated too, so the session copy stays current.
     * Role is never changed here.
     *
     * @return null on success, otherwise a safe error message
     */
    public String updateProfile(User user, String fullName, String email, String university, String newPassword) {
        String name = fullName == null ? user.getFullName() : fullName.trim();
        String mail = email == null ? user.getEmail() : email.trim();
        String uni  = university == null ? user.getUniversity() : university.trim();

        if (InputUtil.isBlank(name) || name.length() > 100) return "Invalid full name.";
        if (!InputUtil.isValidEmail(mail)) return "Invalid email address.";
        if (uni != null && uni.length() > 100) return "University name too long.";
        boolean changePassword = newPassword != null && !newPassword.isEmpty();
        if (changePassword && !InputUtil.isValidPassword(newPassword))
            return "Password must be 8-128 characters.";

        try {
            User other = userDAO.findByEmail(mail);
            if (other != null && other.getId() != user.getId()) return "That email is already in use.";

            User updated = new User();
            updated.setId(user.getId());
            updated.setFullName(name);
            updated.setEmail(mail);
            updated.setUniversity(uni);
            userDAO.updateProfile(updated);
            if (changePassword) userDAO.updatePassword(user.getId(), PasswordUtil.hash(newPassword));

            user.setFullName(name);
            user.setEmail(mail);
            user.setUniversity(uni);
            return null;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) return "That email is already in use.";
            e.printStackTrace();
            return "Update failed.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Update failed.";
        }
    }
}
