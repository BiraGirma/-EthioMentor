package com.ethiomentor.dao;

import com.ethiomentor.config.DBConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class GroupDAO {

    public void createGroup(String name, String description, int creatorId)
            throws Exception {

        String sql = """
            INSERT INTO study_groups (name, description, created_by)
            VALUES (?, ?, ?)
        """;

        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, description);
            ps.setInt(3, creatorId);
            ps.executeUpdate();
        }
    }
}
