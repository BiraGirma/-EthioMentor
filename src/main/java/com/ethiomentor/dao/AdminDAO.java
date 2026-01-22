package com.ethiomentor.dao;

import com.ethiomentor.config.DBConfig;
import com.ethiomentor.model.User;
import com.ethiomentor.model.StudyGroup;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    /* ===== USERS ===== */
    public List<User> getAllUsersExcept(int excludeUserId) throws Exception {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, full_name, role, university, mentor_status FROM users WHERE id <> ? ORDER BY id ASC";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, excludeUserId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setFullName(rs.getString("full_name"));
                u.setRole(rs.getString("role"));
                u.setUniversity(rs.getString("university"));
                u.setMentorStatus(rs.getString("mentor_status"));
                users.add(u);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void deleteUser(int userId) throws Exception {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* ===== MENTOR REQUESTS ===== */
    public List<User> getPendingMentorRequests() throws Exception {
        List<User> mentors = new ArrayList<>();
        String sql = "SELECT id, full_name, email, university FROM users WHERE role='mentor' AND mentor_status='PENDING' ORDER BY id ASC";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setFullName(rs.getString("full_name"));
                u.setEmail(rs.getString("email"));
                u.setUniversity(rs.getString("university"));
                mentors.add(u);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mentors;
    }

    public void updateMentorStatus(int userId, String status) throws Exception {
        String sql = "UPDATE users SET mentor_status=? WHERE id=?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* ===== STUDY GROUPS ===== */
    public List<StudyGroup> getAllGroups() throws Exception {
        List<StudyGroup> groups = new ArrayList<>();
        String sql = "SELECT id, name, description, members_count FROM study_groups ORDER BY id ASC";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                StudyGroup g = new StudyGroup();
                g.setId(rs.getInt("id"));
                g.setName(rs.getString("name"));
                g.setDescription(rs.getString("description"));
                g.setMembersCount(rs.getInt("members_count"));
                groups.add(g);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    public void deleteGroup(int groupId) throws Exception {
        String sql = "DELETE FROM study_groups WHERE id=?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* ===== ANALYTICS ===== */
    public int countUsers() throws Exception {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DBConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countGroups() throws Exception {
        String sql = "SELECT COUNT(*) FROM study_groups";
        try (Connection conn = DBConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countMessages() throws Exception {
        String sql = "SELECT COUNT(*) FROM messages";
        try (Connection conn = DBConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
