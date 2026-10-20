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
     * Fetch full study group details (optional extra utility method).
     *
     * @return list of StudyGroup objects
     */
    public List<StudyGroup> getAllGroups() {
        List<StudyGroup> groups = new ArrayList<>();
        String sql = "SELECT sg.id, sg.name, sg.description, sg.created_by, " +
                     "(SELECT COUNT(*) FROM study_group_members m WHERE m.group_id = sg.id) AS members_count " +
                     "FROM study_groups sg ORDER BY sg.id ASC";

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
