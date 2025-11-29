package com.emp_mgmt.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Simple JDBC connection helper that uses environment variables for configuration.
 * Falls back to defaults if environment vars are not set.
 */
public class SimpleDbConnection {

    // Default values (for local/dev)
    private static final String DEFAULT_URL   = "jdbc:mysql://localhost:3306/emp_mgmt?useSSL=false&serverTimezone=UTC";
    private static final String DEFAULT_USER  = "root";
    private static final String DEFAULT_PASS  = "";

    /**
     * Gets a database connection using environment variables, if supplied.
     * Recognized environment variables (case-sensitive):
     *   DB_URL, DB_USER, DB_PASS
     *
     * If they are not set, fallback defaults will be used.
     */
    public static Connection getConnection() throws SQLException {
        String url  = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASS");

        if (url == null || url.isBlank())  url  = DEFAULT_URL;
        if (user == null || user.isBlank()) user = DEFAULT_USER;
        if (pass == null)                  pass = DEFAULT_PASS;

        return DriverManager.getConnection(url, user, pass);
    }

    // Example test
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Connected to MySQL (URL/user/pass from env or defaults).");
            } else {
                System.out.println("Connection is null or closed.");
            }
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
