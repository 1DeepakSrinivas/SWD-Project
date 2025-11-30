package com.emp_mgmt.dao;

import com.emp_mgmt.db.DatabaseConnectionManager;
import com.emp_mgmt.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDAO {

    private final DatabaseConnectionManager dbManager = DatabaseConnectionManager.getInstance();

    public Employee insert(Employee emp, int divisionId, int jobTitleId) throws SQLException {
        if (divisionId <= 0) {
            throw new SQLException("Division ID is required and must be greater than 0.");
        }
        if (jobTitleId <= 0) {
            throw new SQLException("Job Title ID is required and must be greater than 0.");
        }

        String sql = """
            INSERT INTO employees (first_name, last_name, SSN, email)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = dbManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (!divisionExists(conn, divisionId)) {
                    throw new SQLException("Division ID " + divisionId + " does not exist.");
                }
                if (!jobTitleExists(conn, jobTitleId)) {
                    throw new SQLException("Job Title ID " + jobTitleId + " does not exist.");
                }

                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, emp.getFirstName());
                ps.setString(2, emp.getLastName());
                ps.setString(3, emp.getSsn());
                ps.setString(4, emp.getEmail());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        emp.setEmployeeId(rs.getInt(1));
                    }
                }
                ps.close();

                upsertEmployeeDivision(conn, emp.getEmployeeId(), divisionId);
                upsertEmployeeJobTitle(conn, emp.getEmployeeId(), jobTitleId);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }

        emp.setDivisionId(divisionId);
        emp.setJobTitleId(jobTitleId);
        return emp;
    }

    public Optional<Employee> findById(int id) throws SQLException {
        String sql = """
            SELECT e.employee_id, e.first_name, e.last_name, e.SSN, e.email,
                   ed.division_id, ej.job_title_id
            FROM employees e
            LEFT JOIN employee_division ed ON e.employee_id = ed.employee_id
            LEFT JOIN employee_job_titles ej ON e.employee_id = ej.employee_id
            WHERE e.employee_id = ?
            """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapEmployee(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Employee> findBySSN(String ssn) throws SQLException {
        String sql = """
            SELECT e.employee_id, e.first_name, e.last_name, e.SSN, e.email,
                   ed.division_id, ej.job_title_id
            FROM employees e
            LEFT JOIN employee_division ed ON e.employee_id = ed.employee_id
            LEFT JOIN employee_job_titles ej ON e.employee_id = ej.employee_id
            WHERE e.SSN = ?
            """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ssn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapEmployee(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Employee> findByNameFragment(String fragment) throws SQLException {
        String sql = """
            SELECT e.employee_id, e.first_name, e.last_name, e.SSN, e.email,
                   ed.division_id, ej.job_title_id
            FROM employees e
            LEFT JOIN employee_division ed ON e.employee_id = ed.employee_id
            LEFT JOIN employee_job_titles ej ON e.employee_id = ej.employee_id
            WHERE e.first_name LIKE ? OR e.last_name LIKE ?
            ORDER BY e.last_name, e.first_name
            """;
        List<Employee> list = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String escapedFragment = escapeLikeWildcards(fragment);
            String like = "%" + escapedFragment + "%";
            ps.setString(1, like);
            ps.setString(2, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEmployee(rs));
                }
            }
        }
        return list;
    }

    public boolean update(Employee emp, int divisionId, int jobTitleId) throws SQLException {
        if (divisionId <= 0) {
            throw new SQLException("Division ID is required and must be greater than 0.");
        }
        if (jobTitleId <= 0) {
            throw new SQLException("Job Title ID is required and must be greater than 0.");
        }

        String sql = """
            UPDATE employees
            SET first_name = ?, last_name = ?, SSN = ?, email = ?
            WHERE employee_id = ?
            """;

        try (Connection conn = dbManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, emp.getFirstName());
                ps.setString(2, emp.getLastName());
                ps.setString(3, emp.getSsn());
                ps.setString(4, emp.getEmail());
                ps.setInt(5, emp.getEmployeeId());

                int rows = ps.executeUpdate();
                ps.close();

                if (rows > 0) {
                    if (!divisionExists(conn, divisionId)) {
                        throw new SQLException("Division ID " + divisionId + " does not exist.");
                    }
                    if (!jobTitleExists(conn, jobTitleId)) {
                        throw new SQLException("Job Title ID " + jobTitleId + " does not exist.");
                    }

                    clearEmployeeDivision(conn, emp.getEmployeeId());
                    clearEmployeeJobTitle(conn, emp.getEmployeeId());
                    upsertEmployeeDivision(conn, emp.getEmployeeId(), divisionId);
                    upsertEmployeeJobTitle(conn, emp.getEmployeeId(), jobTitleId);
                }

                conn.commit();
                return rows > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean delete(int employeeId) throws SQLException {
        String sql = "DELETE FROM employees WHERE employee_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    private Employee mapEmployee(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setEmployeeId(rs.getInt("employee_id"));
        e.setFirstName(rs.getString("first_name"));
        e.setLastName(rs.getString("last_name"));
        e.setSsn(rs.getString("SSN"));
        e.setEmail(rs.getString("email"));

        int divId = rs.getInt("division_id");
        if (!rs.wasNull()) {
            e.setDivisionId(divId);
        }
        int jobId = rs.getInt("job_title_id");
        if (!rs.wasNull()) {
            e.setJobTitleId(jobId);
        }
        return e;
    }

    private void upsertEmployeeDivision(Connection conn, int employeeId, int divisionId) throws SQLException {
        // simple approach: delete existing and insert one row
        clearEmployeeDivision(conn, employeeId);
        String sql = "INSERT INTO employee_division (employee_id, division_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, divisionId);
            ps.executeUpdate();
        }
    }

    private void clearEmployeeDivision(Connection conn, int employeeId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM employee_division WHERE employee_id = ?")) {
            ps.setInt(1, employeeId);
            ps.executeUpdate();
        }
    }

    private void upsertEmployeeJobTitle(Connection conn, int employeeId, int jobTitleId) throws SQLException {
        clearEmployeeJobTitle(conn, employeeId);
        String sql = "INSERT INTO employee_job_titles (employee_id, job_title_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, jobTitleId);
            ps.executeUpdate();
        }
    }

    private void clearEmployeeJobTitle(Connection conn, int employeeId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM employee_job_titles WHERE employee_id = ?")) {
            ps.setInt(1, employeeId);
            ps.executeUpdate();
        }
    }

    private boolean divisionExists(Connection conn, int divisionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM division WHERE division_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, divisionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private boolean jobTitleExists(Connection conn, int jobTitleId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM job_titles WHERE job_title_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobTitleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private String escapeLikeWildcards(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("\\", "\\\\")
                    .replace("%", "\\%")
                    .replace("_", "\\_");
    }
}
