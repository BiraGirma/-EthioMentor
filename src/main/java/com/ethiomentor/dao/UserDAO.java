<<<<<<< HEAD
package com.ethiomentor.dao;

import com.ethiomentor.config.DBConfig;
import com.ethiomentor.model.User;
import com.ethiomentor.util.PasswordUtil;

import java.sql.*;

public class UserDAO {

	public User authenticate(String email, String password, String role) {
	    if (role != null) role = role.toLowerCase().trim(); // already lowercase
	    String sql = "SELECT * FROM users WHERE email=? AND role=?";

	    try (Connection con = DBConfig.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, email);
	        ps.setString(2, role);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next() && PasswordUtil.verify(password, rs.getString("password"))) {
	                return map(rs);
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}



    public User findByEmail(String email) throws Exception {
        String sql = "SELECT * FROM users WHERE email=?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
            return null;
        }
    }

    public User findByUsername(String username) throws Exception {
        String sql = "SELECT * FROM users WHERE username=?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
            return null;
        }
    }

    public void save(User user) throws Exception {
        String sql = "INSERT INTO users (username, full_name, email, password, role, university) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword()); // already hashed
            ps.setString(5, user.getRole().toLowerCase().trim()); // lowercase
            ps.setString(6, user.getUniversity());
            ps.executeUpdate();
        }
    }

    public void update(User user) throws Exception {
        String sql = """
            UPDATE users
            SET username=?, full_name=?, email=?, password=?, role=?, university=?
            WHERE id=?
        """;

        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword()); // hashed password
            ps.setString(5, user.getRole().toLowerCase().trim()); // lowercase to match DB
            ps.setString(6, user.getUniversity());
            ps.setInt(7, user.getId());

            ps.executeUpdate();
        }
    }

    /**
     * Maps ResultSet to User object.
     */
    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        u.setUniversity(rs.getString("university"));
        return u;
    }
}
=======
package com.ethiomentor.dao;

import com.ethiomentor.config.DBConfig;
import com.ethiomentor.model.User;
import com.ethiomentor.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /* =====================================================
       🔐 AUTHENTICATION (PRIMARY – SECURE)
       ===================================================== */

    /**
     * Authenticate using email OR username.
     * Password is verified using PasswordUtil.
     * Role is NOT trusted from client.
     */
    public User authenticate(String identifier, String password) {
        String sql = """
            SELECT * FROM users
            WHERE email = ? OR username = ?
        """;

        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, identifier);
            ps.setString(2, identifier);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordUtil.verify(password, rs.getString("password"))) {
                    return map(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /* =====================================================
       🔁 AUTHENTICATION (LEGACY – COMPATIBILITY)
       ===================================================== */

    /**
     * Legacy method kept so old code does not break.
     * Role parameter is IGNORED intentionally.
     */
    public User authenticate(String email, String password, String role) {
        return authenticate(email, password);
    }

    /* =====================================================
       USER LOOKUPS
       ===================================================== */

    public User findByEmail(String email) throws Exception {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
            return null;
        }
    }

    public User findByUsername(String username) throws Exception {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
            return null;
        }
    }

    public User findByEmailOrUsername(String identifier) throws Exception {
        String sql = "SELECT * FROM users WHERE email = ? OR username = ?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, identifier);
            ps.setString(2, identifier);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
            return null;
        }
    }

    /* =====================================================
       CREATE / UPDATE
       ===================================================== */

    public void save(User user) throws Exception {
        String sql = """
            INSERT INTO users
            (username, full_name, email, password, role, university)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword()); // already hashed
            ps.setString(5, user.getRole().toLowerCase().trim());
            ps.setString(6, user.getUniversity());

            ps.executeUpdate();
        }
    }

    public void update(User user) throws Exception {
        String sql = """
            UPDATE users
            SET username = ?, full_name = ?, email = ?, password = ?, role = ?, university = ?
            WHERE id = ?
        """;

        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole().toLowerCase().trim());
            ps.setString(6, user.getUniversity());
            ps.setInt(7, user.getId());

            ps.executeUpdate();
        }
    }

    /* =====================================================
       LISTING
       ===================================================== */

    public List<User> getAllOtherUsers(int userId) throws Exception {
        List<User> users = new ArrayList<>();

        String sql = """
            SELECT id, username, full_name, role, university
            FROM users
            WHERE id <> ?
        """;

        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

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
        return users;
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
        try {
            u.setMentorStatus(rs.getString("mentor_status"));
        } catch (SQLException ignored) {}
        return u;
    }
    public List<User> getMenteesByMentor(int mentorUserId) {
        List<User> mentees = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE mentor_id = ?"; // if you have mentor_id in users
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mentorUserId);
            ResultSet rs = ps.executeQuery();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mentees;
    }

    
}
>>>>>>> 8fb5b46 (finilized)
