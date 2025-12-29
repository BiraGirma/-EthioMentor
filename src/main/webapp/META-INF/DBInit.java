package com.ethiomentor.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBInit {

    private static final String SUPER_URL = "jdbc:postgresql://localhost:5433/";
    private static final String SUPER_USER = "postgres";
    private static final String SUPER_PASSWORD = "ABC123TM0";

    private static final String DB_NAME = "ethiomentor";

    public static void init() {
        try {
            Class.forName("org.postgresql.Driver");

            // 1. Create database if not exists
            try (Connection conn = DriverManager.getConnection(SUPER_URL, SUPER_USER, SUPER_PASSWORD);
                 Statement st = conn.createStatement()) {

                String sql = "SELECT 1 FROM pg_database WHERE datname='" + DB_NAME + "'";
                var rs = st.executeQuery(sql);
                if (!rs.next()) {
                    st.executeUpdate("CREATE DATABASE " + DB_NAME);
                    System.out.println("Database created: " + DB_NAME);
                }
            }

            // 2. Connect to database and create tables if missing
            try (Connection conn = DBConfig.getConnection();
                 Statement st = conn.createStatement()) {

                // USERS table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users (
                        id SERIAL PRIMARY KEY,
                        email VARCHAR(100) UNIQUE NOT NULL,
                        password_hash TEXT NOT NULL,
                        role VARCHAR(20) NOT NULL,
                        is_active BOOLEAN DEFAULT TRUE
                    )
                """);

                // MENTOR_PROFILES
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS mentor_profiles (
                        user_id INT PRIMARY KEY REFERENCES users(id),
                        department VARCHAR(50),
                        expertise TEXT,
                        gpa NUMERIC(3,2),
                        availability VARCHAR(100)
                    )
                """);

                // MENTEE_PROFILES
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS mentee_profiles (
                        user_id INT PRIMARY KEY REFERENCES users(id),
                        department VARCHAR(50),
                        year_of_study INT,
                        interests TEXT
                    )
                """);

                // MESSAGES
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS messages (
                        id SERIAL PRIMARY KEY,
                        sender_id INT REFERENCES users(id),
                        receiver_id INT REFERENCES users(id),
                        content TEXT NOT NULL,
                        sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """);

                // STUDY_GROUPS
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS study_groups (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        description TEXT,
                        created_by INT REFERENCES users(id)
                    )
                """);

                System.out.println("Tables created or verified successfully.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Database initialization failed", e);
        }
    }
}
