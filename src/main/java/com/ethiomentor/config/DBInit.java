package com.ethiomentor.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBInit {

    private static final String SUPER_URL = "jdbc:postgresql://localhost:5433/";
    private static final String SUPER_USER = "postgres";
    private static final String SUPER_PASSWORD = "Abgr@#4132";

    private static final String DB_NAME = "ethiomentors";

    public static void init() {
        try {
            Class.forName("org.postgresql.Driver");

            // 1. Create database if it does not exist
            try (Connection conn = DriverManager.getConnection(SUPER_URL, SUPER_USER, SUPER_PASSWORD);
                 Statement st = conn.createStatement()) {

                String sql = "SELECT 1 FROM pg_database WHERE datname='" + DB_NAME + "'";
                var rs = st.executeQuery(sql);
                if (!rs.next()) {
                    st.executeUpdate("CREATE DATABASE " + DB_NAME);
                    System.out.println("Database created: " + DB_NAME);
                }
            }

            // 2. Connect to the new database and create tables if missing
            try (Connection conn = DBConfig.getConnection();
                 Statement st = conn.createStatement()) {

                // USERS table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users (
                        id SERIAL PRIMARY KEY,
                        username VARCHAR(50) UNIQUE NOT NULL,
                        full_name VARCHAR(100) NOT NULL,
                        email VARCHAR(100) UNIQUE NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        role VARCHAR(20) NOT NULL CHECK (role IN ('student','mentor','admin')),
                        university VARCHAR(100),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """);

                // MENTORS table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS mentors (
                        id SERIAL PRIMARY KEY,
                        user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                        expertise VARCHAR(255)
                    )
                """);

                // STUDY_GROUPS table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS study_groups (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        description TEXT,
                        created_by INT REFERENCES users(id),
                        members_count INT DEFAULT 0
                    )
                """);

                // MESSAGES table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS messages (
                        id SERIAL PRIMARY KEY,
                        sender_id INT REFERENCES users(id) ON DELETE CASCADE,
                        recipient_id INT REFERENCES users(id) ON DELETE CASCADE,
                        content TEXT NOT NULL,
                        timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """);

                // Optional: Insert initial admin if not exists
                st.executeUpdate("""
                    INSERT INTO users (username, full_name, email, password, role, university)
                    SELECT 'admin', 'Admin User', 'admin@ethiomentor.com', '$2a$12$N0w3IKmGHRyZZeXQlvh9Oex1PlR39rIWxVn.6tMi2cT6t8R0N1k6i', 'admin', 'Addis Ababa University'
                    WHERE NOT EXISTS (SELECT 1 FROM users WHERE username='admin')
                """);
                st.executeUpdate("""
                       CREATE TABLE IF NOT EXISTS study_group_members (
                		group_id INT REFERENCES study_groups(id) ON DELETE CASCADE,
                		user_id INT REFERENCES users(id) ON DELETE CASCADE,
                		PRIMARY KEY (group_id, user_id)
                		)""");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS study_group_members (\r\n"
                		+ "    group_id INT REFERENCES study_groups(id) ON DELETE CASCADE,\r\n"
                		+ "    user_id INT REFERENCES users(id) ON DELETE CASCADE,\r\n"
                		+ "    PRIMARY KEY (group_id, user_id)\r\n"
                		+ ");");               
                System.out.println("Tables created or verified successfully.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Database initialization failed", e);
        }
    }
}
