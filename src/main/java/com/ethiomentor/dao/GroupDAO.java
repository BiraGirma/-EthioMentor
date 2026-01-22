<<<<<<< HEAD
package com.ethiomentor.dao;

import com.ethiomentor.config.DBConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class GroupDAO {

    public void createGroup(String name, String description, int creatorId)
            throws Exception {

        String sql = """
            INSERT INTO study_groups (name, description, created_by)
            VALUES (?, ?, ?)
        """;

        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, description);
            ps.setInt(3, creatorId);
            ps.executeUpdate();
        }
    }
}
=======
package com.ethiomentor.dao;

import com.ethiomentor.config.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO {

    // Get all groups created by a mentor
    public List<String> getGroupsCreatedByMentor(int mentorId) {
        List<String> groups = new ArrayList<>();
        String sql = "SELECT name FROM study_groups WHERE created_by = ?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mentorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                groups.add(rs.getString("name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return groups;
    }

    // Create a new study group
    public void createGroup(String name, String description, int creatorId) throws Exception {
        String sql = "INSERT INTO study_groups (name, description, created_by) VALUES (?, ?, ?)";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, description);
            ps.setInt(3, creatorId);
            ps.executeUpdate();
        }
    }
}
>>>>>>> 8fb5b46 (finilized)
