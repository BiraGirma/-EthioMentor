package com.ethiomentor.config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConfig {

    private static final String URL =
        "jdbc:postgresql://localhost:5433/ethiomentors";
    private static final String USER = "postgres";
<<<<<<< HEAD
    private static final String PASSWORD = "Abgr@#4132";
=======
    private static final String PASSWORD = "ABC123TM0";
>>>>>>> 8fb5b46 (finilized)

    public static Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
