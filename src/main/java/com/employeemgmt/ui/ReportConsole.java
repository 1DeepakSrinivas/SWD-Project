package com.employeemgmt.ui;

import com.employeemgmt.model.Employee;
import com.employeemgmt.model.Payroll;
import com.employeemgmt.service.ReportService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class ReportConsole {

    private final Scanner scanner;
    private final ReportService reportService;

    public ReportConsole(Scanner scanner, ReportService reportService) {
        this.scanner = scanner;
        this.reportService = reportService;
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
        int empId = readInt("Employee ID: ");

        Optional<Employee> opt = reportService.getEmployee(empId);
        if (opt.isEmpty()) {
            System.out.println("No employee found with that ID.");
            return;
        }
        Employee e = opt.get();
        List<Payroll> records = reportService.getPayHistoryForEmployee(empId);

        System.out.println("\nEMPLOYEE INFO");
        System.out.println("----------------------------------------");
        System.out.println("ID: " + e.getEmployeeId());
        System.out.println("Name: " + e.getFirstName() + " " + e.getLastName());
        System.out.println("SSN: " + e.getSsn());
        System.out.println("Email: " + e.getEmail());
        System.out.println("----------------------------------------");

        System.out.println("PAY HISTORY");
        System.out.println("----------------------------------------");
        System.out.printf("%-12s %-12s %12s%n", "Start", "End", "Amount");
        for (Payroll p : records) {
            System.out.printf("%-12s %-12s %12.2f%n",
                    p.getPayPeriodStart(),
                    p.getPayPeriodEnd(),
                    p.getAmount().doubleValue());
        }
        System.out.println("----------------------------------------");
    }

    private void showTotalPayByJobTitle() throws SQLException {
        System.out.println("\n--- TOTAL PAY BY JOB TITLE ---");
        int year = readInt("Year (e.g., 2025): ");
        int month = readInt("Month (1-12): ");

        Map<String, BigDecimal> totals = reportService.getTotalPayByJobTitle(year, month);
        if (totals.isEmpty()) {
            System.out.println("No data for that period.");
            return;
        }

        System.out.printf("Total Pay by Job Title for %d-%02d%n", year, month);
        System.out.println("-------------------------------------------");
        System.out.printf("%-30s %15s%n", "Job Title", "Total Pay");
        System.out.println("-------------------------------------------");
        totals.forEach((title, total) ->
                System.out.printf("%-30s %15.2f%n", title, total.doubleValue()));
        System.out.println("-------------------------------------------");
    }

    private void showTotalPayByDivision() throws SQLException {
        System.out.println("\n--- TOTAL PAY BY DIVISION ---");
        int year = readInt("Year (e.g., 2025): ");
        int month = readInt("Month (1-12): ");

        Map<String, BigDecimal> totals = reportService.getTotalPayByDivision(year, month);
        if (totals.isEmpty()) {
            System.out.println("No data for that period.");
            return;
        }

        System.out.printf("Total Pay by Division for %d-%02d%n", year, month);
        System.out.println("-------------------------------------------");
        System.out.printf("%-30s %15s%n", "Division", "Total Pay");
        System.out.println("-------------------------------------------");
        totals.forEach((divName, total) ->
                System.out.printf("%-30s %15.2f%n", divName, total.doubleValue()));
        System.out.println("-------------------------------------------");
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
}
