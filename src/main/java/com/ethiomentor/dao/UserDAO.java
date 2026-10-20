package com.ethiomentor.dao;

import com.ethiomentor.config.DBConfig;
import com.ethiomentor.model.User;
import com.ethiomentor.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /* =====================================================
       AUTHENTICATION
       ===================================================== */

    /**
     * Authenticate using email OR username.
     * Returns null for unknown user, wrong password or a deactivated account.
     * The returned User never carries the password hash (the find* methods do, for callers that verify it).
     * Legacy SHA-256 hashes are transparently upgraded to PBKDF2 on successful login.
     */
    public User authenticate(String identifier, String password) {
        if (identifier == null || password == null) return null;

        String sql = "SELECT * FROM users WHERE email = ? OR username = ?";

        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, identifier);
            ps.setString(2, identifier);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String stored = rs.getString("password");
                    User u = map(rs);
                    boolean ok = PasswordUtil.verify(password, stored);
                    if (ok && u.isActive()) {
                        if (PasswordUtil.needsUpgrade(stored)) {
                            updatePassword(u.getId(), PasswordUtil.hash(password));
                        }
                        u.setPassword(null);   // never hand the hash to callers that put the user in a session
                        return u;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Role argument is ignored; the role always comes from the database. */
    public User authenticate(String email, String password, String role) {
        return authenticate(email, password);
    }

    /* =====================================================
       USER LOOKUPS
       ===================================================== */

    public User findById(int id) throws Exception {
        return findOne("SELECT * FROM users WHERE id = ?", ps -> ps.setInt(1, id));
    }

    public User findByEmail(String email) throws Exception {
        return findOne("SELECT * FROM users WHERE email = ?", ps -> ps.setString(1, email));
    }

    public User findByUsername(String username) throws Exception {
        return findOne("SELECT * FROM users WHERE username = ?", ps -> ps.setString(1, username));
    }

    public User findByEmailOrUsername(String identifier) throws Exception {
        return findOne("SELECT * FROM users WHERE email = ? OR username = ?", ps -> {
            ps.setString(1, identifier);
            ps.setString(2, identifier);
        });
    }

    private interface Binder { void bind(PreparedStatement ps) throws SQLException; }

    private User findOne(String sql, Binder binder) throws Exception {
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /* =====================================================
       CREATE / UPDATE
       ===================================================== */

    /**
     * Inserts the user (password must already be hashed) and, for mentors, their
     * expertise, in one transaction. Sets the generated id on the user.
     */
    public void save(User user, String expertise) throws Exception {
        String sql = "INSERT INTO users (username, full_name, email, password, role, university) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection con = DBConfig.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, user.getUsername());
                    ps.setString(2, user.getFullName());
                    ps.setString(3, user.getEmail());
                    ps.setString(4, user.getPassword());
                    ps.setString(5, user.getRole().toLowerCase().trim());
                    ps.setString(6, user.getUniversity());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Insert returned no id");
                        user.setId(rs.getInt(1));
                    }
                }

                if ("mentor".equalsIgnoreCase(user.getRole()) && expertise != null && !expertise.isBlank()) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO mentors (user_id, expertise) VALUES (?, ?)")) {
                        ps.setInt(1, user.getId());
                        ps.setString(2, expertise);
                        ps.executeUpdate();
                    }
                }
                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }

    /** Updates profile fields only. Role and password are deliberately not touched here. */
    public void updateProfile(User user) throws Exception {
        String sql = "UPDATE users SET full_name = ?, email = ?, university = ? WHERE id = ?";

        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getUniversity());
            ps.setInt(4, user.getId());
            ps.executeUpdate();
        }
    }

    /** @param passwordHash an already-hashed password */
    public void updatePassword(int userId, String passwordHash) throws Exception {
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE users SET password = ? WHERE id = ?")) {
            ps.setString(1, passwordHash);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    /** False if the account is deactivated or no longer exists. */
    public boolean isActive(int userId) throws Exception {
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT is_active FROM users WHERE id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        }
    }

    /** Returns the stored hash for a user id (needed to check the current password), or null. */
    public String getPasswordHash(int userId) throws Exception {
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT password FROM users WHERE id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    /* =====================================================
       LISTING
       ===================================================== */

    /** Active users other than the given one (the candidates for a chat). */
    public List<User> getAllOtherUsers(int userId) throws Exception {
        List<User> users = new ArrayList<>();

        String sql = "SELECT id, username, full_name, role, university FROM users " +
                     "WHERE id <> ? AND is_active ORDER BY full_name";

        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setFullName(rs.getString("full_name"));
                    u.setRole(rs.getString("role"));
                    u.setUniversity(rs.getString("university"));
                    users.add(u);
                }
            }
        }
        return users;
    }

    /**
     * Students who share a private conversation with the given mentor.
     * (The schema has no explicit mentor-to-mentee assignment, so contact is what defines a mentee.)
     */
    public List<User> getMenteesByMentor(int mentorUserId) {
        List<User> mentees = new ArrayList<>();
        String sql =
            "SELECT DISTINCT u.id, u.username, u.full_name, u.email, u.role, u.university " +
            "FROM users u " +
            "JOIN conversation_participants theirs ON theirs.user_id = u.id " +
            "JOIN conversation_participants mine ON mine.conversation_id = theirs.conversation_id AND mine.user_id = ? " +
            "JOIN conversations c ON c.id = theirs.conversation_id AND c.type = 'PRIVATE' " +
            "WHERE u.role = 'student' AND u.id <> ?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mentorUserId);
            ps.setInt(2, mentorUserId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setFullName(rs.getString("full_name"));
                    u.setEmail(rs.getString("email"));
                    u.setRole(rs.getString("role"));
                    u.setUniversity(rs.getString("university"));
                    mentees.add(u);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mentees;
    }

    /* =====================================================
       MAPPER
       ===================================================== */

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        u.setUniversity(rs.getString("university"));
        u.setMentorStatus(rs.getString("mentor_status"));
        u.setActive(rs.getBoolean("is_active"));
        return u;
    }
}
