package com.ethiomentor.dao;

import com.ethiomentor.config.DBConfig;
import com.ethiomentor.model.MentorProfile;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MentorDAO {

    /**
     * Mentors shown in Mentor Matching: admin-approved and not deactivated.
     * (Newly registered mentors are PENDING until an admin approves them.)
     */
    public List<MentorProfile> getAllMentors() throws SQLException {
        List<MentorProfile> mentors = new ArrayList<>();
        String sql = "SELECT u.id, u.full_name, u.username, u.email, u.university, " +
                     "       (SELECT string_agg(m.expertise, ',') FROM mentors m WHERE m.user_id = u.id) AS expertise " +
                     "FROM users u " +
                     "WHERE u.role = 'mentor' AND u.mentor_status = 'APPROVED' AND u.is_active " +
                     "ORDER BY u.full_name";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MentorProfile m = new MentorProfile();
                m.setId(rs.getInt("id"));
                m.setUserId(rs.getInt("id"));
                m.setName(rs.getString("full_name"));
                m.setUsername(rs.getString("username"));
                m.setEmail(rs.getString("email"));
                m.setUniversity(rs.getString("university"));

                String exp = rs.getString("expertise");
                m.setExpertise(exp != null && !exp.isBlank() ? exp.split("\\s*,\\s*") : new String[]{});
                mentors.add(m);
            }
        }
        return mentors;
    }

    /** Stores a mentor's comma-separated expertise (replaces any previous value). */
    public void saveExpertise(int userId, String expertiseCsv) throws SQLException {
        try (Connection conn = DBConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement del = conn.prepareStatement("DELETE FROM mentors WHERE user_id = ?");
                 PreparedStatement ins = conn.prepareStatement("INSERT INTO mentors (user_id, expertise) VALUES (?, ?)")) {
                del.setInt(1, userId);
                del.executeUpdate();
                ins.setInt(1, userId);
                ins.setString(2, expertiseCsv);
                ins.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
