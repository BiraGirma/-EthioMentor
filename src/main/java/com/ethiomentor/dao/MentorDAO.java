package com.ethiomentor.dao;

import com.ethiomentor.config.DBConfig;
import com.ethiomentor.model.MentorProfile;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MentorDAO {

    // Fetch all mentors
    public List<MentorProfile> getAllMentors() {
        List<MentorProfile> mentors = new ArrayList<>();
        String sql = "SELECT u.id, u.full_name, u.username, u.email, u.university, m.expertise " +
                     "FROM users u LEFT JOIN mentors m ON m.user_id = u.id " +
                     "WHERE u.role = 'mentor'";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("Executing SQL: " + sql);

            while (rs.next()) {
                MentorProfile m = new MentorProfile();
                m.setId(rs.getInt("id"));
                m.setName(rs.getString("full_name"));
                m.setUsername(rs.getString("username"));
                m.setEmail(rs.getString("email"));
                m.setUniversity(rs.getString("university"));

                String exp = rs.getString("expertise");
                if (exp != null && !exp.isEmpty()) {
                    m.setExpertise(exp.split(","));
                } else {
                    m.setExpertise(new String[]{});
                }

                System.out.println("Fetched Mentor -> ID: " + m.getId() + ", Name: " + m.getName() + ", Username: " + m.getUsername());

                mentors.add(m);
            }

            if (mentors.isEmpty()) {
                System.out.println("No mentors found in the database.");
            } else {
                System.out.println("Total mentors fetched: " + mentors.size());
            }

        } catch (SQLException e) {
            System.err.println("SQL Exception while fetching mentors: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }

        return mentors;
    }
}
