package com.ethiomentor.dao;

import com.ethiomentor.config.DBConfig;
import com.ethiomentor.model.StudyGroup;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Member counts are computed from study_group_members, so they can never drift
 * (the legacy study_groups.members_count column is no longer read or written).
 */
public class StudyGroupDAO {

    private static final String MEMBER_COUNT =
            "(SELECT COUNT(*) FROM study_group_members mc WHERE mc.group_id = sg.id) AS members_count";

    /** All groups, newest first, flagged with whether the user is a member. */
    public List<StudyGroup> getAllGroups(int userId) {
        String sql = "SELECT sg.id, sg.name, sg.description, sg.created_by, " + MEMBER_COUNT + ", " +
                     "EXISTS (SELECT 1 FROM study_group_members m WHERE m.group_id = sg.id AND m.user_id = ?) AS is_member " +
                     "FROM study_groups sg ORDER BY sg.id DESC";
        return query(sql, userId);
    }

    public List<StudyGroup> getRandomGroups(int userId) {
        List<StudyGroup> all = getAllGroups(userId);
        Collections.shuffle(all);
        return all.size() <= 5 ? all : new ArrayList<>(all.subList(0, 5));
    }

    public List<StudyGroup> getJoinedGroups(int userId) {
        String sql = "SELECT sg.id, sg.name, sg.description, sg.created_by, " + MEMBER_COUNT + ", TRUE AS is_member " +
                     "FROM study_groups sg JOIN study_group_members m ON sg.id = m.group_id " +
                     "WHERE m.user_id = ? ORDER BY sg.id DESC";
        return query(sql, userId);
    }

    public List<StudyGroup> getCreatedGroups(int userId) {
        String sql = "SELECT sg.id, sg.name, sg.description, sg.created_by, " + MEMBER_COUNT + ", " +
                     "EXISTS (SELECT 1 FROM study_group_members m WHERE m.group_id = sg.id AND m.user_id = ?) AS is_member " +
                     "FROM study_groups sg WHERE sg.created_by = ? ORDER BY sg.id DESC";
        return query(sql, userId, userId);
    }

    private List<StudyGroup> query(String sql, int... params) {
        List<StudyGroup> groups = new ArrayList<>();
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setInt(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StudyGroup g = new StudyGroup();
                    g.setId(rs.getInt("id"));
                    g.setName(rs.getString("name"));
                    g.setDescription(rs.getString("description"));
                    g.setCreatedBy(rs.getInt("created_by"));
                    g.setMembersCount(rs.getInt("members_count"));
                    g.setMember(rs.getBoolean("is_member"));
                    groups.add(g);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groups;
    }

    /** Creates the group and adds the creator as its first member, atomically. */
    public int createGroup(String name, String description, int userId) throws SQLException {
        try (Connection con = DBConfig.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ins = con.prepareStatement(
                         "INSERT INTO study_groups (name, description, created_by) VALUES (?, ?, ?) RETURNING id");
                 PreparedStatement mem = con.prepareStatement(
                         "INSERT INTO study_group_members (group_id, user_id) VALUES (?, ?)")) {
                ins.setString(1, name);
                ins.setString(2, description);
                ins.setInt(3, userId);
                int groupId;
                try (ResultSet rs = ins.executeQuery()) {
                    rs.next();
                    groupId = rs.getInt(1);
                }
                mem.setInt(1, groupId);
                mem.setInt(2, userId);
                mem.executeUpdate();
                con.commit();
                return groupId;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    /** @return false if the group does not exist; joining twice is a harmless no-op. */
    public boolean joinGroup(int groupId, int userId) throws SQLException {
        String sql = "INSERT INTO study_group_members (group_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ps.setInt(2, userId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) return false; // foreign_key_violation: no such group
            throw e;
        }
    }

    public int getMembersCount(int groupId) {
        String sql = "SELECT COUNT(*) FROM study_group_members WHERE group_id = ?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
