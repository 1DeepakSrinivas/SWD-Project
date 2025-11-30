package com.employeemgmt.ui;

import com.employeemgmt.model.Division;
import com.employeemgmt.model.Employee;
import com.employeemgmt.model.JobTitle;
import com.employeemgmt.service.EmployeeService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class EmployeeConsole {

    private final Scanner scanner;
    private final EmployeeService employeeService;

    public EmployeeConsole(Scanner scanner, EmployeeService employeeService) {
        this.scanner = scanner;
        this.employeeService = employeeService;
    }

    public void showMenu() {
        boolean back = false;
        while (!back) {
            printEmployeeMenu();
            int choice = readInt("Choose an option: ");
            try {
                switch (choice) {
                    case 1 -> addEmployee();
                    case 2 -> searchEmployee();
                    case 3 -> updateEmployee();
                    case 4 -> deleteEmployee();
                    case 5 -> applySalaryIncrease();
                    case 0 -> back = true;
                    default -> System.out.println("Invalid choice. Try again.");
                }
            } catch (SQLException ex) {
                System.out.println("Database error: " + ex.getMessage());
            }
        }
    }

    private void printEmployeeMenu() {
        System.out.println("\n----- EMPLOYEE MENU -----");
        System.out.println("1. Add Employee");
        System.out.println("2. Search Employee");
        System.out.println("3. Update Employee");
        System.out.println("4. Delete Employee");
        System.out.println("5. Salary Increase (by range)");
        System.out.println("0. Back to Main Menu");
    }

    private void addEmployee() throws SQLException {
        System.out.println("\n--- ADD EMPLOYEE ---");
        String firstName = readNonEmpty("First Name: ");
        String lastName = readNonEmpty("Last Name: ");
        String ssn = readSSN();
        String email = readNonEmpty("Email: ");

        // Divisions
        List<Division> divisions = employeeService.getAllDivisions();
        System.out.println("Available Divisions:");
        for (Division d : divisions) {
            System.out.printf("  %d: %s%n", d.getDivisionId(), d.getName());
        }
        int divisionId = readInt("Division ID: ");

        // Job titles
        List<JobTitle> jobTitles = employeeService.getAllJobTitles();
        System.out.println("Available Job Titles:");
        for (JobTitle jt : jobTitles) {
            System.out.printf("  %d: %s%n", jt.getJobTitleId(), jt.getTitle());
        }
        int jobTitleId = readInt("Job Title ID: ");

        Employee emp = new Employee();
        emp.setFirstName(firstName);
        emp.setLastName(lastName);
        emp.setSsn(ssn);
        emp.setEmail(email);

        Employee saved = employeeService.addEmployee(emp, divisionId, jobTitleId);
        System.out.println("Employee created with ID: " + saved.getEmployeeId());
    }

    private void searchEmployee() throws SQLException {
        System.out.println("\n--- SEARCH EMPLOYEE ---");
        System.out.println("1. By Employee ID");
        System.out.println("2. By SSN");
        System.out.println("3. By Name (fragment)");
        System.out.println("0. Back");
        int choice = readInt("Choose: ");

        switch (choice) {
            case 1 -> searchById();
            case 2 -> searchBySSN();
            case 3 -> searchByName();
            case 0 -> { }
            default -> System.out.println("Invalid choice.");
        }
    }

    private void searchById() throws SQLException {
        int id = readInt("Employee ID: ");
        Optional<Employee> opt = employeeService.findById(id);
        if (opt.isPresent()) {
            printEmployee(opt.get());
        } else {
            System.out.println("No employee found with ID " + id);
        }
    }

    private void searchBySSN() throws SQLException {
        String ssn = readSSN();
        Optional<Employee> opt = employeeService.findBySSN(ssn);
        if (opt.isPresent()) {
            printEmployee(opt.get());
        } else {
            System.out.println("No employee found with SSN " + ssn);
        }
    }

    private void searchByName() throws SQLException {
        String fragment = readNonEmpty("Enter name fragment (first or last): ");
        List<Employee> employees = employeeService.findByNameFragment(fragment);
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
        } else {
            System.out.println("Found " + employees.size() + " employee(s):");
            employees.forEach(this::printEmployeeOneLine);
        }
    }

    private void updateEmployee() throws SQLException {
        System.out.println("\n--- UPDATE EMPLOYEE ---");
        int id = readInt("Employee ID to update: ");
        Optional<Employee> opt = employeeService.findById(id);
        if (opt.isEmpty()) {
            System.out.println("No employee with that ID.");
            return;
        }

        Employee emp = opt.get();
        System.out.println("Current data:");
        printEmployee(emp);

        System.out.println("\nEnter new values (leave blank to keep current).");
        String firstName = readOptional("First Name [" + emp.getFirstName() + "]: ");
        String lastName = readOptional("Last Name [" + emp.getLastName() + "]: ");
        String ssn = readOptional("SSN (" + emp.getSsn() + "): ");
        String email = readOptional("Email [" + emp.getEmail() + "]: ");

        if (!firstName.isEmpty()) emp.setFirstName(firstName);
        if (!lastName.isEmpty()) emp.setLastName(lastName);
        if (!ssn.isEmpty()) emp.setSsn(ssn);
        if (!email.isEmpty()) emp.setEmail(email);

        boolean success = employeeService.updateEmployee(emp, 0, 0);
        System.out.println(success ? "Employee updated." : "Update failed.");
    }

    private void deleteEmployee() throws SQLException {
        System.out.println("\n--- DELETE EMPLOYEE ---");
        int id = readInt("Employee ID to delete: ");
        String confirm = readNonEmpty("Are you sure you want to delete this employee? (Y/N): ");
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Delete cancelled.");
            return;
        }
        boolean success = employeeService.deleteEmployee(id);
        System.out.println(success ? "Employee deleted." : "Delete failed.");
    }

    private void applySalaryIncrease() throws SQLException {
        System.out.println("\n--- SALARY INCREASE (RANGE) ---");
        BigDecimal min = readBigDecimal("Minimum salary (inclusive): ");
        BigDecimal max = readBigDecimal("Maximum salary (inclusive): ");
        BigDecimal pct = readBigDecimal("Percentage increase (e.g., 3.2 for 3.2%): ");

        int updated = employeeService.increaseSalaryInRange(min, max, pct);
        System.out.println("Updated " + updated + " employee(s).");
    }

    // Helpers

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

    private BigDecimal readBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return new BigDecimal(line);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid decimal number.");
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

    private String readOptional(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private String readSSN() {
        while (true) {
            String ssn = readNonEmpty("SSN (9 digits, no dashes): ");
            if (ssn.matches("\\d{9}")) return ssn;
            System.out.println("SSN must be exactly 9 digits, no dashes.");
        }
    }

    private void printEmployee(Employee e) {
        System.out.println("----------------------------------------");
        System.out.println("ID: " + e.getEmployeeId());
        System.out.println("Name: " + e.getFirstName() + " " + e.getLastName());
        System.out.println("SSN: " + e.getSsn());
        System.out.println("Email: " + e.getEmail());
        System.out.println("----------------------------------------");
    }

    private void printEmployeeOneLine(Employee e) {
        System.out.printf("ID=%d | %s %s | SSN=%s | Email=%s%n",
                e.getEmployeeId(),
                e.getFirstName(),
                e.getLastName(),
                e.getSsn(),
                e.getEmail());
    }
}
