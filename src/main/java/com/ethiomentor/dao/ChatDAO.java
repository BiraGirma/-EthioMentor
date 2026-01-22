package com.ethiomentor.dao;

import com.ethiomentor.config.DBConfig;
import com.ethiomentor.model.ChatMessage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatDAO {

<<<<<<< HEAD
=======
<<<<<<< HEAD
    public ChatMessage saveMessage(ChatMessage message) throws Exception {
        String sql = "INSERT INTO messages (conversation_id, sender_id, recipient_id, content, type, edited) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING id, timestamp";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, message.getConversationId());
            ps.setInt(2, message.getSenderId());
            if (message.getRecipientId() != null) ps.setInt(3, message.getRecipientId());
            else ps.setNull(3, Types.INTEGER);
            ps.setString(4, message.getContent());
            ps.setString(5, message.getType());
            ps.setBoolean(6, message.isEdited());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                message.setId(rs.getInt("id"));
                message.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                return message;
            }
        }
        return null;
    }

    public List<ChatMessage> getMessagesByConversation(int conversationId, int limit, int offset) throws Exception {
        List<ChatMessage> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE conversation_id=? ORDER BY timestamp DESC LIMIT ? OFFSET ?";
=======
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
	public ChatMessage saveMessage(ChatMessage message) throws Exception {
	    String sql = "INSERT INTO messages (conversation_id, sender_id, recipient_id, content, type, edited) " +
	                 "VALUES (?, ?, ?, ?, ?, ?) RETURNING id, timestamp";
	    try (Connection conn = DBConfig.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, message.getConversationId());
	        ps.setInt(2, message.getSenderId());
	        if (message.getRecipientId() != null) ps.setInt(3, message.getRecipientId());
	        else ps.setNull(3, java.sql.Types.INTEGER);
	        ps.setString(4, message.getContent());
	        ps.setString(5, message.getType());
	        ps.setBoolean(6, message.isEdited());

	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            message.setId(rs.getInt("id"));
	            message.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
	            return message; // This ensures it's saved
	        }
	    }
	    return null;
	}


    public List<ChatMessage> getMessagesByConversation(int conversationId, int limit, int offset) throws Exception {
        List<ChatMessage> messages = new ArrayList<>();
        String sql ="SELECT * FROM messages WHERE conversation_id=? ORDER BY timestamp ASC LIMIT ? OFFSET ?";

<<<<<<< HEAD
=======
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, conversationId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ChatMessage m = new ChatMessage();
                m.setId(rs.getInt("id"));
                m.setConversationId(rs.getInt("conversation_id"));
                m.setSenderId(rs.getInt("sender_id"));
                m.setRecipientId(rs.getObject("recipient_id") != null ? rs.getInt("recipient_id") : null);
                m.setContent(rs.getString("content"));
                m.setType(rs.getString("type"));
                m.setEdited(rs.getBoolean("edited"));
                m.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                messages.add(m);
            }
        }
        return messages;
    }
}
