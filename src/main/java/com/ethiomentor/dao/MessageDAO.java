<<<<<<< HEAD
package com.ethiomentor.dao;

import com.ethiomentor.config.DBConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class MessageDAO {

    public void sendMessage(int senderId, int receiverId, String content)
            throws Exception {

        String sql = """
            INSERT INTO messages (sender_id, receiver_id, content)
            VALUES (?, ?, ?)
        """;

        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.setString(3, content);
            ps.executeUpdate();
        }
    }
}
=======
package com.ethiomentor.dao;

import com.ethiomentor.config.DBConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class MessageDAO {

    public void sendMessage(int senderId, int receiverId, String content)
            throws Exception {

        String sql = """
            INSERT INTO messages (sender_id, receiver_id, content)
            VALUES (?, ?, ?)
        """;

        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.setString(3, content);
            ps.executeUpdate();
        }
    }
}
>>>>>>> 8fb5b46 (finilized)
