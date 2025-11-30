package com.emp_mgmt.ui;

import com.emp_mgmt.dto.DivisionPay;
import com.emp_mgmt.dto.EmployeeWithPayHistory;
import com.emp_mgmt.dto.JobTitlePay;
import com.emp_mgmt.model.Employee;
import com.emp_mgmt.service.EmployeeService;
import com.emp_mgmt.service.ReportService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ReportConsole {

    private final Scanner scanner;
    private final ReportService reportService;
    private final EmployeeService employeeService;

    public ReportConsole(Scanner scanner,
                         ReportService reportService,
                         EmployeeService employeeService) {
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
                    case 1 -> showFtePayHistory();
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
        System.out.println("\n----- REPORTS MENU -----");
        System.out.println("1. FTE Info + Pay History");
        System.out.println("2. Total Pay by Job Title (for a month)");
        System.out.println("3. Total Pay by Division (for a month)");
        System.out.println("0. Back to Main Menu");
    }

    private void showFtePayHistory() throws SQLException {
        System.out.println("\n--- FTE INFO + PAY HISTORY ---");
        System.out.println("1. Select by Employee ID");
        System.out.println("2. Select by SSN");
        System.out.println("0. Back");
        int choice = readInt("Choose: ");

        Integer employeeId = null;
        if (choice == 1) {
            employeeId = readInt("Employee ID: ");
        } else if (choice == 2) {
            String ssn = readNonEmpty("SSN (9 digits, no dashes): ");
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
            System.out.println("No data for that employee.");
            return;
        }
        printEmployeeWithPayHistory(dto);
    }

    private void showTotalPayByJobTitle() throws SQLException {
        System.out.println("\n--- TOTAL PAY BY JOB TITLE ---");
        int year = readInt("Year (e.g., 2025): ");
        int month = readInt("Month (1-12): ");

        List<JobTitlePay> rows = reportService.getTotalPayByJobTitle(year, month);
        if (rows.isEmpty()) {
            System.out.println("No data for that period.");
            return;
        }

        System.out.printf("Total Pay by Job Title for %d-%02d%n", year, month);
        System.out.println("-------------------------------------------");
        System.out.printf("%-30s %15s%n", "Job Title", "Total Pay");
        System.out.println("-------------------------------------------");
        for (JobTitlePay r : rows) {
            System.out.printf("%-30s %15.2f%n",
                    r.getJobTitleName(),
                    r.getTotalPay().doubleValue());
        }
        System.out.println("-------------------------------------------");
    }

    private void showTotalPayByDivision() throws SQLException {
        System.out.println("\n--- TOTAL PAY BY DIVISION ---");
        int year = readInt("Year (e.g., 2025): ");
        int month = readInt("Month (1-12): ");

        List<DivisionPay> rows = reportService.getTotalPayByDivision(year, month);
        if (rows.isEmpty()) {
            System.out.println("No data for that period.");
            return;
        }

        System.out.printf("Total Pay by Division for %d-%02d%n", year, month);
        System.out.println("-------------------------------------------");
        System.out.printf("%-30s %15s%n", "Division", "Total Pay");
        System.out.println("-------------------------------------------");
        for (DivisionPay r : rows) {
            System.out.printf("%-30s %15.2f%n",
                    r.getDivisionName(),
                    r.getTotalPay().doubleValue());
        }
        System.out.println("-------------------------------------------");
    }

    // helpers
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
            if (!line.isEmpty()) return line;
            System.out.println("Value is required.");
        }
    }

    private void printEmployeeWithPayHistory(EmployeeWithPayHistory dto) {
        Employee e = dto.getEmployee();
        System.out.println("\nEMPLOYEE INFO");
        System.out.println("----------------------------------------");
        System.out.println("ID: " + e.getEmployeeId());
        System.out.println("Name: " + e.getFirstName() + " " + e.getLastName());
        System.out.println("SSN: " + e.getSsn());
        System.out.println("Email: " + e.getEmail());
        System.out.println("Division: " + dto.getDivisionName());
        System.out.println("Job Title: " + dto.getJobTitleName());
        System.out.println("----------------------------------------");

        System.out.println("PAY HISTORY");
        System.out.println("----------------------------------------");
        System.out.printf("%-12s %-12s %12s%n", "Start", "End", "Amount");
        dto.getPayRecords().forEach(r -> System.out.printf(
                "%-12s %-12s %12.2f%n",
                r.getPeriodStart(),
                r.getPeriodEnd(),
                r.getAmount().doubleValue()
        ));
        System.out.println("----------------------------------------");
    }
}
