package com.yourorg.employee;

import static org.junit.Assert.*;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import com.yourorg.employee.db.DatabaseConnection;

public class DatabaseConnectionTest {

    @Test
    public void testCanConnect() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            assertNotNull("Connection should not be null", conn);
            assertFalse("Connection should not be closed", conn.isClosed());
        } catch (SQLException e) {
            fail("Should have connected, but got exception: " + e.getMessage());
        }
    }
}

