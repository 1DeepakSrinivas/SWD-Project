package com.emp_mgmt.dao;

import com.emp_mgmt.db.DatabaseConnectionManager;
import com.emp_mgmt.dto.DivisionPay;
import com.emp_mgmt.dto.EmployeeWithPayHistory;
import com.emp_mgmt.dto.JobTitlePay;
import com.emp_mgmt.model.Employee;
import com.emp_mgmt.model.PayrollRecord;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    private final DatabaseConnectionManager dbManager = DatabaseConnectionManager.getInstance();

    public EmployeeWithPayHistory getEmployeeWithPayHistory(int employeeId) throws SQLException {
        String sql = """
            SELECT e.employee_id, e.first_name, e.last_name, e.SSN, e.email,
                   d.name AS division_name,
                   jt.title AS job_title_name,
                   p.payroll_id, p.amount, p.pay_period_start, p.pay_period_end
            FROM employees e
            LEFT JOIN employee_division ed ON e.employee_id = ed.employee_id
            LEFT JOIN division d ON ed.division_id = d.division_id
            LEFT JOIN employee_job_titles ej ON e.employee_id = ej.employee_id
            LEFT JOIN job_titles jt ON ej.job_title_id = jt.job_title_id
            LEFT JOIN payroll p ON e.employee_id = p.employee_id
            WHERE e.employee_id = ?
            ORDER BY p.pay_period_start
            """;

        EmployeeWithPayHistory result = new EmployeeWithPayHistory();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                boolean firstRow = true;
                List<PayrollRecord> payRecords = new ArrayList<>();

                while (rs.next()) {
                    if (firstRow) {
                        Employee e = new Employee();
                        e.setEmployeeId(rs.getInt("employee_id"));
                        e.setFirstName(rs.getString("first_name"));
                        e.setLastName(rs.getString("last_name"));
                        e.setSsn(rs.getString("SSN"));
                        e.setEmail(rs.getString("email"));

                        result.setEmployee(e);
                        result.setDivisionName(rs.getString("division_name"));
                        result.setJobTitleName(rs.getString("job_title_name"));
                        firstRow = false;
                    }

                    int payrollId = rs.getInt("payroll_id");
                    if (!rs.wasNull()) {
                        PayrollRecord pr = new PayrollRecord();
                        pr.setPayrollId(payrollId);
                        pr.setEmployeeId(employeeId);
                        pr.setAmount(rs.getBigDecimal("amount"));

                        Date start = rs.getDate("pay_period_start");
                        Date end = rs.getDate("pay_period_end");
                        if (start != null) {
                            pr.setPeriodStart(start.toLocalDate());
                        }
                        if (end != null) {
                            pr.setPeriodEnd(end.toLocalDate());
                        }
                        payRecords.add(pr);
                    }
                }

                result.setPayRecords(payRecords);
            }
        }

        return result;
    }

    public List<JobTitlePay> getTotalPayByJobTitle(int year, int month) throws SQLException {
        String sql = """
            SELECT jt.title, SUM(p.amount) AS total_pay
            FROM payroll p
            JOIN employees e ON p.employee_id = e.employee_id
            JOIN employee_job_titles ej ON e.employee_id = ej.employee_id
            JOIN job_titles jt ON ej.job_title_id = jt.job_title_id
            WHERE YEAR(p.pay_period_start) = ? AND MONTH(p.pay_period_start) = ?
            GROUP BY jt.title
            ORDER BY jt.title
            """;

        List<JobTitlePay> list = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setInt(2, month);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String title = rs.getString("title");
                    BigDecimal total = rs.getBigDecimal("total_pay");
                    list.add(new JobTitlePay(title, total));
                }
            }
        }
        return list;
    }

    public List<DivisionPay> getTotalPayByDivision(int year, int month) throws SQLException {
        String sql = """
            SELECT d.name AS division_name, SUM(p.amount) AS total_pay
            FROM payroll p
            JOIN employees e ON p.employee_id = e.employee_id
            JOIN employee_division ed ON e.employee_id = ed.employee_id
            JOIN division d ON ed.division_id = d.division_id
            WHERE YEAR(p.pay_period_start) = ? AND MONTH(p.pay_period_start) = ?
            GROUP BY d.name
            ORDER BY d.name
            """;

        List<DivisionPay> list = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setInt(2, month);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("division_name");
                    BigDecimal total = rs.getBigDecimal("total_pay");
                    list.add(new DivisionPay(name, total));
                }
            }
        }
        return list;
    }
}
