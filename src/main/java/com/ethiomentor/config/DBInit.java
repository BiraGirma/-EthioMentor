package com.ethiomentor.config;

import com.ethiomentor.util.PasswordUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBInit {

    private static final String SUPER_URL = "jdbc:postgresql://localhost:5433/";
    private static final String SUPER_USER = "postgres";
    private static final String SUPER_PASSWORD = "ABC123TM0";

    private static final String DB_NAME = "ethiomentors";

    public static void init() {
        try {
            // Print SHA-256 hash of default admin password (admin123)
            System.out.println("Admin password hash: " + PasswordUtil.hash("admin123"));

            Class.forName("org.postgresql.Driver");

            // 1️⃣ Create database if it does not exist
            try (Connection conn = DriverManager.getConnection(SUPER_URL, SUPER_USER, SUPER_PASSWORD);
                 Statement st = conn.createStatement()) {

                String sql = "SELECT 1 FROM pg_database WHERE datname='" + DB_NAME + "'";
                ResultSet rs = st.executeQuery(sql);
                if (!rs.next()) {
                    st.executeUpdate("CREATE DATABASE " + DB_NAME);
                    System.out.println("Database created: " + DB_NAME);
                }
            }

            // 2️⃣ Connect to the new database and create tables if missing
            try (Connection conn = DBConfig.getConnection();
                 Statement st = conn.createStatement()) {

                // USERS table with mentor_status
                st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS users (" +
                        "id SERIAL PRIMARY KEY," +
                        "username VARCHAR(50) UNIQUE NOT NULL," +
                        "full_name VARCHAR(100) NOT NULL," +
                        "email VARCHAR(100) UNIQUE NOT NULL," +
                        "password VARCHAR(255) NOT NULL," +
                        "role VARCHAR(20) NOT NULL CHECK (role IN ('student','mentor','admin'))," +
                        "university VARCHAR(100)," +
                        "mentor_status VARCHAR(20) DEFAULT 'PENDING'," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")"
                );

                // Ensure mentor_status exists if table already exists
                addColumnIfMissing(st, "users", "mentor_status", "VARCHAR(20) DEFAULT 'PENDING'");

                // Mentors table
                st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS mentors (" +
                        "id SERIAL PRIMARY KEY," +
                        "user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE," +
                        "expertise VARCHAR(255)" +
                    ")"
                );

                // Study groups
                st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS study_groups (" +
                        "id SERIAL PRIMARY KEY," +
                        "name VARCHAR(100) NOT NULL," +
                        "description TEXT," +
                        "created_by INT REFERENCES users(id)," +
                        "members_count INT DEFAULT 0" +
                    ")"
                );

                st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS study_group_members (" +
                        "group_id INT REFERENCES study_groups(id) ON DELETE CASCADE," +
                        "user_id INT REFERENCES users(id) ON DELETE CASCADE," +
                        "PRIMARY KEY (group_id, user_id)" +
                    ")"
                );

                // Conversations
                st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS conversations (" +
                        "id SERIAL PRIMARY KEY," +
                        "type VARCHAR(20) NOT NULL CHECK (type IN ('PRIVATE','GROUP'))," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")"
                );

                st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS conversation_participants (" +
                        "conversation_id INT REFERENCES conversations(id) ON DELETE CASCADE," +
                        "user_id INT REFERENCES users(id) ON DELETE CASCADE," +
                        "joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "PRIMARY KEY (conversation_id, user_id)" +
                    ")"
                );

                // Messages
                st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS messages (" +
                        "id SERIAL PRIMARY KEY," +
                        "sender_id INT REFERENCES users(id) ON DELETE CASCADE," +
                        "recipient_id INT REFERENCES users(id) ON DELETE CASCADE," +
                        "content TEXT NOT NULL," +
                        "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")"
                );

                addColumnIfMissing(st, "messages", "type", "VARCHAR(20) DEFAULT 'TEXT'");
                addColumnIfMissing(st, "messages", "edited", "BOOLEAN DEFAULT FALSE");
                addColumnIfMissing(st, "messages", "conversation_id", "INT REFERENCES conversations(id) ON DELETE CASCADE");

                // Message status
                st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS message_status (" +
                        "message_id INT REFERENCES messages(id) ON DELETE CASCADE," +
                        "user_id INT REFERENCES users(id) ON DELETE CASCADE," +
                        "status VARCHAR(20) NOT NULL CHECK (status IN ('SENT','DELIVERED','READ'))," +
                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "PRIMARY KEY (message_id, user_id)" +
                    ")"
                );

                // Group conversations
                st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS group_conversations (" +
                        "group_id INT REFERENCES study_groups(id) ON DELETE CASCADE," +
                        "conversation_id INT REFERENCES conversations(id) ON DELETE CASCADE," +
                        "PRIMARY KEY (group_id, conversation_id)" +
                    ")"
                );

                // Insert default admin user if not exists
                st.executeUpdate(
                    "INSERT INTO users (username, full_name, email, password, role, university) " +
                        "SELECT 'admin', 'Admin User', 'admin@ethiomentor.com', " +
                        "'240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', " +
                        "'admin', 'Addis Ababa University' " +
                        "WHERE NOT EXISTS (SELECT 1 FROM users WHERE username='admin')"
                );

                // Indexes
                st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_messages_conversation ON messages(conversation_id, timestamp DESC)");
                st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_message_status_user ON message_status(user_id, status)");

                System.out.println("Database and tables initialized successfully.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    /**
     * Safely add a column if it does not exist
     */
    private static void addColumnIfMissing(Statement st, String table, String column, String definition) throws Exception {
        ResultSet rs = st.executeQuery(
                "SELECT column_name FROM information_schema.columns WHERE table_name='" + table + "' AND column_name='" + column + "'"
        );
        if (!rs.next()) {
            st.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
            System.out.println("Added missing column: " + table + "." + column);
        }
    }
}
