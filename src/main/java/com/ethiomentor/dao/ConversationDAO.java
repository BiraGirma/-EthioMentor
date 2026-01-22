package com.ethiomentor.dao;

import com.ethiomentor.config.DBConfig;
import com.ethiomentor.model.Conversation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConversationDAO {

    public Conversation createConversation(String type) throws Exception {
        String sql = "INSERT INTO conversations (type) VALUES (?) RETURNING id, created_at";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Conversation c = new Conversation();
                c.setId(rs.getInt("id"));
                c.setType(type);
                c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return c;
            }
        }
        return null;
    }

    public void addParticipant(int conversationId, int userId) throws Exception {

        if (userId <= 0) {
            throw new SQLException("Invalid userId: " + userId);
        }

        String sql =
            "INSERT INTO conversation_participants (conversation_id, user_id) " +
            "VALUES (?, ?) ON CONFLICT DO NOTHING";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, conversationId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    
    public Integer getPrivateConversationId(int user1, int user2) throws Exception {
        String sql = "SELECT c.id FROM conversations c " +
                     "JOIN conversation_participants p1 ON c.id = p1.conversation_id " +
                     "JOIN conversation_participants p2 ON c.id = p2.conversation_id " +
                     "WHERE c.type='PRIVATE' AND p1.user_id=? AND p2.user_id=?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user1);
            ps.setInt(2, user2);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
            return null;
        }
    }

    public List<Conversation> getUserConversations(int userId) throws Exception {
        List<Conversation> list = new ArrayList<>();
        String sql = "SELECT c.id, c.type, c.created_at " +
                     "FROM conversations c " +
                     "JOIN conversation_participants p ON c.id = p.conversation_id " +
                     "WHERE p.user_id = ?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Conversation c = new Conversation();
                c.setId(rs.getInt("id"));
                c.setType(rs.getString("type"));
                c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                list.add(c);
            }
        }
        return list;
    }


    public List<Integer> getParticipants(int conversationId) throws Exception {
        List<Integer> participants = new ArrayList<>();
        String sql = "SELECT user_id FROM conversation_participants WHERE conversation_id=?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, conversationId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                participants.add(rs.getInt("user_id"));
            }
        }
        return participants;
    }
}
