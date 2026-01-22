package com.ethiomentor.dao;

import com.ethiomentor.config.DBConfig;
import com.ethiomentor.model.StudyGroup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for managing study groups.
 */
public class GroupDAO {

    /**
     * Fetch all study group names created by a specific mentor.
     *
     * @param mentorId the mentor's user ID
     * @return list of study group names
     */
    public List<String> getGroupsCreatedByMentor(int mentorId) {
        List<String> groups = new ArrayList<>();
        String sql = "SELECT name FROM study_groups WHERE created_by = ? ORDER BY id ASC";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mentorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    groups.add(rs.getString("name"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return groups;
    }

    /**
     * Create a new study group.
     *
     * @param name        the group name
     * @param description the group description
     * @param creatorId   the user ID of the mentor creating the group
     * @throws Exception in case of DB errors
     */
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

    /**
     * Fetch full study group details (optional extra utility method).
     *
     * @return list of StudyGroup objects
     */
    public List<StudyGroup> getAllGroups() {
        List<StudyGroup> groups = new ArrayList<>();
        String sql = "SELECT id, name, description, members_count, created_by FROM study_groups ORDER BY id ASC";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                StudyGroup g = new StudyGroup();
                g.setId(rs.getInt("id"));
                g.setName(rs.getString("name"));
                g.setDescription(rs.getString("description"));
                g.setMembersCount(rs.getInt("members_count"));
                g.setCreatedBy(rs.getInt("created_by"));
                groups.add(g);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return groups;
    }
}
