package com.emp_mgmt.ui;

import com.emp_mgmt.dto.DivisionPay;
import com.emp_mgmt.dto.EmployeeWithPayHistory;
import com.emp_mgmt.dto.JobTitlePay;
import com.emp_mgmt.model.Employee;
import com.emp_mgmt.service.EmployeeService;
import com.emp_mgmt.service.ReportService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ReportConsole {

    private final Scanner scanner;
    private final ReportService reportService;
    private final EmployeeService employeeService;

    public ReportConsole(Scanner scanner, ReportService reportService, EmployeeService employeeService) {
        this.scanner = scanner;
        this.reportService = reportService;
        this.employeeService = employeeService;
    }

    public void showMenu() {
        boolean back = false;
        while (!back) {
            printReportMenu();
            int choice = readInt("Choose an option: ");
            try {
                switch (choice) {
                    case 1 -> showEmployeePayHistory();
                    case 2 -> showTotalPayByJobTitle();
                    case 3 -> showTotalPayByDivision();
                    case 0 -> back = true;
                    default -> System.out.println("Invalid choice. Try again.");
                }
            } catch (SQLException ex) {
                System.out.println("Database error: " + ex.getMessage());
            }
        }
    }

    private void printReportMenu() {
        System.out.println("\n--- REPORTS ---");
        System.out.println("1. Full-time Employee Information with Pay History");
        System.out.println("2. Total Pay by Job Title (for a month)");
        System.out.println("3. Total Pay by Division (for a month)");
        System.out.println("0. Back to Main Menu");
    }

    private void showEmployeePayHistory() throws SQLException {
        System.out.println("\n--- FULL-TIME EMPLOYEE INFO + PAY HISTORY ---");
        System.out.println("Find employee by:");
        System.out.println("1. Employee ID");
        System.out.println("2. SSN");
        System.out.println("0. Back");

        int choice = readInt("Choose: ");
        Integer employeeId = null;

        if (choice == 1) {
            employeeId = readInt("Enter Employee ID: ");
        } else if (choice == 2) {
            String ssn = readSSN();
            Optional<Employee> opt = employeeService.findBySSN(ssn);
            if (opt.isEmpty()) {
                System.out.println("No employee found with that SSN.");
                return;
            }
            employeeId = opt.get().getEmployeeId();
        } else if (choice == 0) {
            return;
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        EmployeeWithPayHistory dto = reportService.getEmployeeWithPayHistory(employeeId);
        if (dto.getEmployee() == null) {
            System.out.println("No data found for that employee.");
            return;
        }

        Employee e = dto.getEmployee();
        System.out.println("\n========================================");
        System.out.println("EMPLOYEE INFORMATION");
        System.out.println("========================================");
        System.out.println("Employee ID: " + e.getEmployeeId());
        System.out.println("Name: " + e.getFirstName() + " " + e.getLastName());
        System.out.println("SSN: " + e.getSsn());
        System.out.println("Email: " + e.getEmail());
        System.out.println("Division: " + (dto.getDivisionName() != null ? dto.getDivisionName() : "N/A"));
        System.out.println("Job Title: " + (dto.getJobTitleName() != null ? dto.getJobTitleName() : "N/A"));
        System.out.println("========================================");

        System.out.println("\nPAY HISTORY");
        System.out.println("========================================");
        if (dto.getPayRecords().isEmpty()) {
            System.out.println("No pay records found.");
        } else {
            System.out.printf("%-15s %-15s %15s%n", "Period Start", "Period End", "Amount");
            System.out.println("-----------------------------------------------");
            dto.getPayRecords().forEach(r -> System.out.printf(
                    "%-15s %-15s $%14.2f%n",
                    r.getPeriodStart(),
                    r.getPeriodEnd(),
                    r.getAmount().doubleValue()
            ));
        }
        System.out.println("========================================");
    }

    private void showTotalPayByJobTitle() throws SQLException {
        System.out.println("\n--- TOTAL PAY BY JOB TITLE ---");
        int year = readInt("Enter year (e.g., 2025): ");
        int month = readInt("Enter month (1-12): ");

        if (month < 1 || month > 12) {
            System.out.println("Invalid month. Must be between 1 and 12.");
            return;
        }

        List<JobTitlePay> rows = reportService.getTotalPayByJobTitle(year, month);
        if (rows.isEmpty()) {
            System.out.println("No data found for " + year + "-" + String.format("%02d", month) + ".");
            return;
        }

        System.out.println("\n========================================");
        System.out.printf("TOTAL PAY BY JOB TITLE - %d-%02d%n", year, month);
        System.out.println("========================================");
        System.out.printf("%-40s %15s%n", "Job Title", "Total Pay");
        System.out.println("-----------------------------------------------");

        BigDecimal total = BigDecimal.ZERO;
        for (JobTitlePay r : rows) {
            System.out.printf("%-40s $%14.2f%n",
                    r.getJobTitleName(),
                    r.getTotalPay().doubleValue());
            total = total.add(r.getTotalPay());
        }
        System.out.println("-----------------------------------------------");
        System.out.printf("%-40s $%14.2f%n", "TOTAL", total.doubleValue());
        System.out.println("========================================");
    }

    private void showTotalPayByDivision() throws SQLException {
        System.out.println("\n--- TOTAL PAY BY DIVISION ---");
        int year = readInt("Enter year (e.g., 2025): ");
        int month = readInt("Enter month (1-12): ");

        if (month < 1 || month > 12) {
            System.out.println("Invalid month. Must be between 1 and 12.");
            return;
        }

        List<DivisionPay> rows = reportService.getTotalPayByDivision(year, month);
        if (rows.isEmpty()) {
            System.out.println("No data found for " + year + "-" + String.format("%02d", month) + ".");
            return;
        }

        System.out.println("\n========================================");
        System.out.printf("TOTAL PAY BY DIVISION - %d-%02d%n", year, month);
        System.out.println("========================================");
        System.out.printf("%-40s %15s%n", "Division", "Total Pay");
        System.out.println("-----------------------------------------------");

        BigDecimal total = BigDecimal.ZERO;
        for (DivisionPay r : rows) {
            System.out.printf("%-40s $%14.2f%n",
                    r.getDivisionName(),
                    r.getTotalPay().doubleValue());
            total = total.add(r.getTotalPay());
        }
        System.out.println("-----------------------------------------------");
        System.out.printf("%-40s $%14.2f%n", "TOTAL", total.doubleValue());
        System.out.println("========================================");
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Value is required.");
        }
    }

    private String readSSN() {
        while (true) {
            String ssn = readNonEmpty("Enter SSN (9 digits, no dashes): ");
            if (ssn.matches("\\d{9}")) {
                return ssn;
            }
            System.out.println("SSN must be exactly 9 digits, no dashes.");
        }
    }
}
