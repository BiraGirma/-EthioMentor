package com.ethiomentor.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection settings.
 *
 * Nothing secret lives in source control. Values are read from environment
 * variables (or, as a fallback, JVM system properties with the same name):
 *
 *   DB_URL       full JDBC URL (overrides host/port/name when set)
 *   DB_HOST      default "localhost"
 *   DB_PORT      default "5432"
 *   DB_NAME      default "ethiomentors"
 *   DB_USER      default "postgres"
 *   DB_PASSWORD  no default
 *
 * See config/setenv.sh.example for how to provide them to Tomcat.
 */
public class DBConfig {

    private DBConfig() {}

    public static String get(String name, String defaultValue) {
        String v = System.getenv(name);
        if (v == null || v.isEmpty()) v = System.getProperty(name);
        return (v == null || v.isEmpty()) ? defaultValue : v;
    }

    public static String host()     { return get("DB_HOST", "localhost"); }
    public static String port()     { return get("DB_PORT", "5432"); }
    public static String name()     { return get("DB_NAME", "ethiomentors"); }
    public static String user()     { return get("DB_USER", "postgres"); }
    public static String password() { return get("DB_PASSWORD", ""); }

    public static String url() {
        String explicit = get("DB_URL", null);
        if (explicit != null) return explicit;
        return "jdbc:postgresql://" + host() + ":" + port() + "/" + name();
    }

    /** URL of the always-present maintenance database, used only to create the app database. */
    public static String maintenanceUrl() {
        return "jdbc:postgresql://" + host() + ":" + port() + "/postgres";
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC driver not found on the classpath", e);
        }
        return DriverManager.getConnection(url(), user(), password());
    }
}
