package com.ethiomentor.config;

import com.ethiomentor.util.PasswordUtil;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Creates the database (when permitted) and any missing tables/columns at startup.
 * Every statement is additive and idempotent: nothing is dropped or rewritten.
 */
public class DBInit {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_EMAIL = "admin@ethiomentor.com";

    public static void init() {
        try {
            Class.forName("org.postgresql.Driver");

            if (DBConfig.password().isEmpty()) {
                System.err.println("[EthioMentor] WARNING: DB_PASSWORD is not set. "
                        + "Connecting without a password (works only with trust/peer auth).");
            }

            createDatabaseIfMissing();
            createSchema();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    /** Best effort: needs CREATEDB rights. If the database already exists nothing happens. */
    private static void createDatabaseIfMissing() {
        // An explicit DB_URL means the operator manages the database themselves.
        if (DBConfig.get("DB_URL", null) != null) return;

        try (Connection conn = DriverManager.getConnection(
                     DBConfig.maintenanceUrl(), DBConfig.user(), DBConfig.password());
             PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM pg_database WHERE datname = ?")) {

            ps.setString(1, DBConfig.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    // Identifier cannot be bound as a parameter; restrict it to a safe charset instead.
                    if (!DBConfig.name().matches("[A-Za-z_][A-Za-z0-9_]{0,62}")) {
                        throw new IllegalStateException("Unsafe DB_NAME: " + DBConfig.name());
                    }
                    try (Statement st = conn.createStatement()) {
                        st.executeUpdate("CREATE DATABASE " + DBConfig.name());
                        System.out.println("[EthioMentor] Database created: " + DBConfig.name());
                    }
                }
            }
        } catch (Exception e) {
            // Not fatal: createSchema() will fail loudly if the database really is unreachable.
            System.err.println("[EthioMentor] Could not check/create database (" + e.getMessage()
                    + "). Assuming it already exists.");
        }
    }

    private static void createSchema() throws Exception {
        try (Connection conn = DBConfig.getConnection();
             Statement st = conn.createStatement()) {

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
                    "is_active BOOLEAN NOT NULL DEFAULT TRUE," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");

            // Upgrades for databases created by earlier versions
            st.executeUpdate("ALTER TABLE users ADD COLUMN IF NOT EXISTS mentor_status VARCHAR(20) DEFAULT 'PENDING'");
            st.executeUpdate("ALTER TABLE users ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT TRUE");

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS mentors (" +
                    "id SERIAL PRIMARY KEY," +
                    "user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE," +
                    "expertise VARCHAR(255)" +
                ")");

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS study_groups (" +
                    "id SERIAL PRIMARY KEY," +
                    "name VARCHAR(100) NOT NULL," +
                    "description TEXT," +
                    "created_by INT REFERENCES users(id)," +
                    "members_count INT DEFAULT 0" +   // legacy column; counts are now computed from study_group_members
                ")");

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS study_group_members (" +
                    "group_id INT REFERENCES study_groups(id) ON DELETE CASCADE," +
                    "user_id INT REFERENCES users(id) ON DELETE CASCADE," +
                    "PRIMARY KEY (group_id, user_id)" +
                ")");

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS conversations (" +
                    "id SERIAL PRIMARY KEY," +
                    "type VARCHAR(20) NOT NULL CHECK (type IN ('PRIVATE','GROUP'))," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS conversation_participants (" +
                    "conversation_id INT REFERENCES conversations(id) ON DELETE CASCADE," +
                    "user_id INT REFERENCES users(id) ON DELETE CASCADE," +
                    "joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "PRIMARY KEY (conversation_id, user_id)" +
                ")");

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS messages (" +
                    "id SERIAL PRIMARY KEY," +
                    "sender_id INT REFERENCES users(id) ON DELETE CASCADE," +
                    "recipient_id INT REFERENCES users(id) ON DELETE CASCADE," +
                    "content TEXT NOT NULL," +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");

            st.executeUpdate("ALTER TABLE messages ADD COLUMN IF NOT EXISTS type VARCHAR(20) DEFAULT 'TEXT'");
            st.executeUpdate("ALTER TABLE messages ADD COLUMN IF NOT EXISTS edited BOOLEAN DEFAULT FALSE");
            st.executeUpdate("ALTER TABLE messages ADD COLUMN IF NOT EXISTS conversation_id INT REFERENCES conversations(id) ON DELETE CASCADE");

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS message_status (" +
                    "message_id INT REFERENCES messages(id) ON DELETE CASCADE," +
                    "user_id INT REFERENCES users(id) ON DELETE CASCADE," +
                    "status VARCHAR(20) NOT NULL CHECK (status IN ('SENT','DELIVERED','READ'))," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "PRIMARY KEY (message_id, user_id)" +
                ")");

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS group_conversations (" +
                    "group_id INT REFERENCES study_groups(id) ON DELETE CASCADE," +
                    "conversation_id INT REFERENCES conversations(id) ON DELETE CASCADE," +
                    "PRIMARY KEY (group_id, conversation_id)" +
                ")");

            st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_messages_conversation ON messages(conversation_id, timestamp DESC)");
            st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_message_status_user ON message_status(user_id, status)");

            seedAdmin(conn);
            warnIfDefaultAdminPassword(conn);

            System.out.println("[EthioMentor] Database and tables initialized successfully.");
        }
    }

    /**
     * Creates the first administrator if none named "admin" exists.
     * Password comes from ADMIN_INITIAL_PASSWORD; if unset a random one is generated and
     * printed once to the server log.
     */
    private static void seedAdmin(Connection conn) throws Exception {
        try (PreparedStatement check = conn.prepareStatement("SELECT 1 FROM users WHERE username = ?")) {
            check.setString(1, ADMIN_USERNAME);
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) return;
            }
        }

        String password = DBConfig.get("ADMIN_INITIAL_PASSWORD", null);
        boolean generated = false;
        if (password == null) {
            password = randomPassword();
            generated = true;
        }

        try (PreparedStatement ins = conn.prepareStatement(
                "INSERT INTO users (username, full_name, email, password, role, university) " +
                "VALUES (?, 'Admin User', ?, ?, 'admin', 'Addis Ababa University') " +
                "ON CONFLICT DO NOTHING")) {
            ins.setString(1, ADMIN_USERNAME);
            ins.setString(2, ADMIN_EMAIL);
            ins.setString(3, PasswordUtil.hash(password));
            if (ins.executeUpdate() == 1) {
                System.out.println("[EthioMentor] Created administrator account '" + ADMIN_USERNAME + "'.");
                if (generated) {
                    System.out.println("[EthioMentor] Generated one-time admin password: " + password
                            + "   (set ADMIN_INITIAL_PASSWORD to choose your own; change it after first login)");
                }
            }
        }
    }

    /** Earlier versions seeded admin/admin123. Warn loudly if that account still exists unchanged. */
    private static void warnIfDefaultAdminPassword(Connection conn) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("SELECT password FROM users WHERE username = ?")) {
            ps.setString(1, ADMIN_USERNAME);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordUtil.verify("admin123", rs.getString(1))) {
                    System.err.println("[EthioMentor] SECURITY WARNING: the 'admin' account still uses the old "
                            + "default password. Log in and change it immediately.");
                }
            }
        }
    }

    private static String randomPassword() {
        final String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
        SecureRandom r = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) sb.append(alphabet.charAt(r.nextInt(alphabet.length())));
        return sb.toString();
    }
}
