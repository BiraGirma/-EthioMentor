package com.ethiomentor.service;

import com.ethiomentor.config.DBConfig;

import java.sql.Connection;
import java.sql.Statement;

public class AdminService {

    public void deactivateUser(int userId) throws Exception {
        try (Connection con = DBConfig.getConnection();
             Statement st = con.createStatement()) {

            st.executeUpdate(
                "UPDATE users SET is_active = false WHERE id = " + userId
            );
        }
    }
}
