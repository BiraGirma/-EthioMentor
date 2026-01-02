package com.ethiomentor.dao;

import com.ethiomentor.model.MentorProfile;
import com.ethiomentor.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MentorDAO {

    // Fetch all mentors
    public List<MentorProfile> getAllMentors() {
        List<MentorProfile> mentors = new ArrayList<>();

        String sql = "SELECT m.id, m.user_id, u.full_name, u.username, u.email, u.university, m.expertise " +
                     "FROM mentors m JOIN users u ON m.user_id = u.id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MentorProfile mentor = new MentorProfile();
                mentor.setId(rs.getInt("id"));
                mentor.setUserId(rs.getInt("user_id"));
                mentor.setName(rs.getString("full_name"));
                mentor.setUsername(rs.getString("username"));
                mentor.setEmail(rs.getString("email"));
                mentor.setUniversity(rs.getString("university"));

                String expertiseStr = rs.getString("expertise");
                mentor.setExpertise(expertiseStr != null ? expertiseStr.split(",") : new String[]{});

                mentors.add(mentor);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mentors;
    }

    // Save a new mentor profile
    public void saveProfile(MentorProfile profile) throws Exception {
        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psMentor = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // transaction

            // 1. Insert into users table
            String sqlUser = "INSERT INTO users (username, full_name, email, password, role, university) " +
                             "VALUES (?, ?, ?, ?, 'mentor', ?)";
            psUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, profile.getUsername());
            psUser.setString(2, profile.getName());
            psUser.setString(3, profile.getEmail());
            psUser.setString(4, "password123"); // default password; ideally hashed
            psUser.setString(5, profile.getUniversity());
            psUser.executeUpdate();

            generatedKeys = psUser.getGeneratedKeys();
            int userId = 0;
            if (generatedKeys.next()) {
                userId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }

            // 2. Insert into mentors table
            String sqlMentor = "INSERT INTO mentors (user_id, expertise) VALUES (?, ?)";
            psMentor = conn.prepareStatement(sqlMentor);
            psMentor.setInt(1, userId);
            psMentor.setString(2, String.join(",", profile.getExpertise()));
            psMentor.executeUpdate();

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            e.printStackTrace();
            throw new Exception("Failed to save mentor profile: " + e.getMessage());
        } finally {
            if (generatedKeys != null) generatedKeys.close();
            if (psMentor != null) psMentor.close();
            if (psUser != null) psUser.close();
            if (conn != null) conn.close();
        }
    }
}
