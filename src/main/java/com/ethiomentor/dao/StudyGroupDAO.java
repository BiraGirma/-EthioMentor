package com.ethiomentor.dao;

import com.ethiomentor.config.DBConfig;
import com.ethiomentor.model.StudyGroup;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudyGroupDAO {

    // All groups with member info
    public List<StudyGroup> getAllGroups(int userId) {
        List<StudyGroup> groups = new ArrayList<>();
        String sql = "SELECT sg.*, " +
                     "EXISTS (SELECT 1 FROM study_group_members m WHERE m.group_id = sg.id AND m.user_id=?) AS is_member " +
                     "FROM study_groups sg ORDER BY id DESC";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                StudyGroup g = new StudyGroup();
                g.setId(rs.getInt("id"));
                g.setName(rs.getString("name"));
                g.setDescription(rs.getString("description"));
                g.setCreatedBy(rs.getInt("created_by"));
                g.setMembersCount(rs.getInt("members_count"));
                g.setMember(rs.getBoolean("is_member"));
                groups.add(g);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return groups;
    }

    public List<StudyGroup> getRandomGroups(int userId) {
        List<StudyGroup> allGroups = getAllGroups(userId);
        Collections.shuffle(allGroups);
        return allGroups.size() <= 5 ? allGroups : allGroups.subList(0,5);
    }

    public List<StudyGroup> getJoinedGroups(int userId) {
        List<StudyGroup> groups = new ArrayList<>();
        String sql = "SELECT sg.*, true AS is_member " +
                     "FROM study_groups sg " +
                     "JOIN study_group_members m ON sg.id = m.group_id " +
                     "WHERE m.user_id = ?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                StudyGroup g = new StudyGroup();
                g.setId(rs.getInt("id"));
                g.setName(rs.getString("name"));
                g.setDescription(rs.getString("description"));
                g.setCreatedBy(rs.getInt("created_by"));
                g.setMembersCount(rs.getInt("members_count"));
                g.setMember(true);
                groups.add(g);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return groups;
    }

    public List<StudyGroup> getCreatedGroups(int userId) {
        List<StudyGroup> groups = new ArrayList<>();
        String sql = "SELECT sg.*, " +
                     "EXISTS (SELECT 1 FROM study_group_members m WHERE m.group_id = sg.id AND m.user_id=?) AS is_member " +
                     "FROM study_groups sg WHERE sg.created_by = ?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                StudyGroup g = new StudyGroup();
                g.setId(rs.getInt("id"));
                g.setName(rs.getString("name"));
                g.setDescription(rs.getString("description"));
                g.setCreatedBy(rs.getInt("created_by"));
                g.setMembersCount(rs.getInt("members_count"));
                g.setMember(rs.getBoolean("is_member"));
                groups.add(g);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return groups;
    }

    public void createGroup(String name, String description, int userId) throws Exception {
        String sql = "INSERT INTO study_groups (name, description, created_by, members_count) VALUES (?, ?, ?, 0)";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setInt(3, userId);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if(keys.next()) {
                int groupId = keys.getInt(1);
                joinGroup(groupId, userId); // add creator as member
            }
        }
    }

    public void joinGroup(int groupId, int userId) throws Exception {
        String insertSql = "INSERT INTO study_group_members(group_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        String updateSql = "UPDATE study_groups SET members_count = members_count + 1 WHERE id=? AND NOT EXISTS " +
                           "(SELECT 1 FROM study_group_members WHERE group_id=? AND user_id=?)";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement psInsert = con.prepareStatement(insertSql);
             PreparedStatement psUpdate = con.prepareStatement(updateSql)) {

            psInsert.setInt(1, groupId);
            psInsert.setInt(2, userId);
            psInsert.executeUpdate();

            psUpdate.setInt(1, groupId);
            psUpdate.setInt(2, groupId);
            psUpdate.setInt(3, userId);
            psUpdate.executeUpdate();
        }
    }

    public int getMembersCount(int groupId) {
        String sql = "SELECT members_count FROM study_groups WHERE id=?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getInt("members_count");
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
}
