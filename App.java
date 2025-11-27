package com.yourorg.employee;

import com.yourorg.employee.db.DatabaseConnection;
import com.yourorg.employee.model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Connected to database!");

            // Example: create table (if not exists)
            String createTableSql = "CREATE TABLE IF NOT EXISTS employee (" +
                                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                                    "first_name VARCHAR(100)," +
                                    "last_name VARCHAR(100)," +
                                    "department VARCHAR(100)" +
                                    ")";
            conn.createStatement().execute(createTableSql);

            // Example: insert a sample employee
            String insertSql = "INSERT INTO employee (first_name, last_name, department) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setString(1, "John");
                pstmt.setString(2, "Doe");
                pstmt.setString(3, "Engineering");
                pstmt.executeUpdate();
            }

            // Example: retrieve all employees
            String selectSql = "SELECT * FROM employee";
            try (ResultSet rs = conn.createStatement().executeQuery(selectSql)) {
                System.out.println("Employee list:");
                while (rs.next()) {
                    Employee emp = new Employee(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("department")
                    );
                    System.out.println(emp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

