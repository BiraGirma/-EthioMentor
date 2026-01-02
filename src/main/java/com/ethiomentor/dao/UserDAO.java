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
