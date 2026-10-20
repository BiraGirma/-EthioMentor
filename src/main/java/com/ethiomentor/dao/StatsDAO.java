package com.ethiomentor.dao;

import com.ethiomentor.config.DBConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/** Simple counters for the student and mentor dashboards. Returns 0 on error (and logs it). */
public class StatsDAO {

    public int countStudyGroups()       { return count("SELECT COUNT(*) FROM study_groups"); }
    public int countApprovedMentors()   { return count("SELECT COUNT(*) FROM users WHERE role='mentor' AND mentor_status='APPROVED' AND is_active"); }
    public int countStudents()          { return count("SELECT COUNT(*) FROM users WHERE role='student' AND is_active"); }
    public int countConversations()     { return count("SELECT COUNT(*) FROM conversations"); }

    /** Messages in conversations the user takes part in. */
    public int countMessagesFor(int userId) {
        return count("SELECT COUNT(*) FROM messages m JOIN conversation_participants p " +
                     "ON p.conversation_id = m.conversation_id AND p.user_id = ?", userId);
    }

    public int countGroupsJoinedBy(int userId) {
        return count("SELECT COUNT(*) FROM study_group_members WHERE user_id = ?", userId);
    }

    private int count(String sql, int... params) {
        try (Connection c = DBConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setInt(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
