package com.ethiomentor.dao;

import com.ethiomentor.config.DBConfig;
import com.ethiomentor.model.MessageStatus;

import java.sql.*;

public class MessageStatusDAO {

    public void updateStatus(int messageId, int userId, String status) throws Exception {
        String sql = """
            INSERT INTO message_status (message_id, user_id, status)
            VALUES (?, ?, ?)
            ON CONFLICT (message_id, user_id) DO UPDATE
            SET status = EXCLUDED.status,
                updated_at = CURRENT_TIMESTAMP
        """;
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, messageId);
            ps.setInt(2, userId);
            ps.setString(3, status);
            ps.executeUpdate();
        }
    }
}
