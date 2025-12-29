package com.ethiomentor.config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConfig {

    private static final String URL =
        "jdbc:postgresql://localhost:5433/ethiomentor";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Bira@123";

    public static Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
