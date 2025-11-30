package com.emp_mgmt.ui;

import com.emp_mgmt.db.DatabaseConnectionManager;
import com.emp_mgmt.model.Employee;
import com.emp_mgmt.service.EmployeeService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    public void searchEmployee() {
        try {
            System.out.println("\n--- SEARCH EMPLOYEE ---");
            System.out.println("Search by:");
            System.out.println("1. Employee ID");
            System.out.println("2. SSN");
            System.out.println("3. Name");
            System.out.println("0. Back");

            int choice = readInt("Choose: ");

            Optional<Employee> employee = Optional.empty();

            switch (choice) {
                case 1 -> {
                    int id = readInt("Enter Employee ID: ");
                    employee = employeeService.findById(id);
                }
                case 2 -> {
                    String ssn = readSSN();
                    employee = employeeService.findBySSN(ssn);
                }
                case 3 -> {
                    String name = readNonEmpty("Enter name (first or last): ");
                    List<Employee> employees = employeeService.findByName(name);
                    if (employees.isEmpty()) {
                        System.out.println("No employees found.");
                        return;
                    }
                    if (employees.size() == 1) {
                        employee = Optional.of(employees.get(0));
                    } else {
                        System.out.println("\nFound " + employees.size() + " employee(s):");
                        for (int i = 0; i < employees.size(); i++) {
                            System.out.println((i + 1) + ". " + employees.get(i).getFirstName() + " " +
                                             employees.get(i).getLastName() + " (ID: " +
                                             employees.get(i).getEmployeeId() + ")");
                        }
                        int select = readInt("\nSelect employee number: ");
                        if (select > 0 && select <= employees.size()) {
                            employee = Optional.of(employees.get(select - 1));
                        } else {
                            System.out.println("Invalid selection.");
                            return;
                        }
                    }
                }
                case 0 -> {
                    return;
                }
                default -> {
                    System.out.println("Invalid choice.");
                    return;
                }
            }

            if (employee.isPresent()) {
                printEmployeeDetails(employee.get());
            } else {
                System.out.println("Employee not found.");
            }
        } catch (SQLException ex) {
            System.out.println("Database error: " + ex.getMessage());
        }
    }

    public void updateEmployee() {
        try {
            System.out.println("\n--- UPDATE EMPLOYEE ---");
            System.out.println("Find employee by:");
            System.out.println("1. Employee ID");
            System.out.println("2. SSN");
            System.out.println("0. Back");

            int searchChoice = readInt("Choose: ");
            Optional<Employee> opt = Optional.empty();

            switch (searchChoice) {
                case 1 -> {
                    int id = readInt("Enter Employee ID: ");
                    opt = employeeService.findById(id);
                }
                case 2 -> {
                    String ssn = readSSN();
                    opt = employeeService.findBySSN(ssn);
                }
                case 0 -> {
                    return;
                }
                default -> {
                    System.out.println("Invalid choice.");
                    return;
                }
            }

            if (opt.isEmpty()) {
                System.out.println("Employee not found.");
                return;
            }

            Employee emp = opt.get();
            System.out.println("\nCurrent employee information:");
            printEmployeeDetails(emp);

            System.out.println("\nEnter new values (press Enter to keep current value):");
            String firstName = readOptional("First Name [" + emp.getFirstName() + "]: ");
            String lastName = readOptional("Last Name [" + emp.getLastName() + "]: ");
            String ssn = readOptional("SSN [" + emp.getSsn() + "]: ");
            String email = readOptional("Email [" + emp.getEmail() + "]: ");
            
            String divisionName = emp.getDivisionId() != null ? getDivisionName(emp.getDivisionId()) : "none";
            String jobTitleName = emp.getJobTitleId() != null ? getJobTitleName(emp.getJobTitleId()) : "none";
            
            String divisionStr = readOptional("Division ID [" +
                    (emp.getDivisionId() == null ? "none" : emp.getDivisionId() + " (" + divisionName + ")") + "]: ");
            String jobTitleStr = readOptional("Job Title ID [" +
                    (emp.getJobTitleId() == null ? "none" : emp.getJobTitleId() + " (" + jobTitleName + ")") + "]: ");

            if (!firstName.isEmpty()) {
                emp.setFirstName(firstName);
            }
            if (!lastName.isEmpty()) {
                emp.setLastName(lastName);
            }
            if (!ssn.isEmpty()) {
                emp.setSsn(ssn);
            }
            if (!email.isEmpty()) {
                emp.setEmail(email);
            }

            int divisionId;
            if (divisionStr.isEmpty()) {
                if (emp.getDivisionId() == null) {
                    System.out.println("Error: Division ID is required. Employee must have a division.");
                    return;
                }
                divisionId = emp.getDivisionId();
            } else {
                divisionId = Integer.parseInt(divisionStr);
            }

            int jobTitleId;
            if (jobTitleStr.isEmpty()) {
                if (emp.getJobTitleId() == null) {
                    System.out.println("Error: Job Title ID is required. Employee must have a job title.");
                    return;
                }
                jobTitleId = emp.getJobTitleId();
            } else {
                jobTitleId = Integer.parseInt(jobTitleStr);
            }

            boolean success = employeeService.updateEmployee(emp, divisionId, jobTitleId);
            if (success) {
                System.out.println("\nEmployee updated successfully.");
                System.out.println("Updated information:");
                Employee updated = employeeService.findById(emp.getEmployeeId()).orElse(emp);
                printEmployeeDetails(updated);
            } else {
                System.out.println("Update failed.");
            }
        } catch (SQLException ex) {
            System.out.println("Database error: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public void updateSalaryByPercentage() {
        try {
            System.out.println("\n--- UPDATE SALARY BY PERCENTAGE ---");
            System.out.println("This will increase payroll amounts within a specified range.");
            System.out.println("Example: 3.2% increase for salaries >= $58,000 and < $105,000\n");

            double percentage = readDouble("Enter percentage increase (e.g., 3.2 for 3.2%): ");
            BigDecimal min = readBigDecimal("Minimum salary amount (inclusive): $");
            BigDecimal max = readBigDecimal("Maximum salary amount (exclusive): $");

            if (min.compareTo(max) >= 0) {
                System.out.println("Error: Minimum must be less than maximum.");
                return;
            }

            System.out.println("\nApplying " + percentage + "% increase to payroll amounts");
            System.out.println("between $" + min + " (inclusive) and $" + max + " (exclusive)...");

            int rowsAffected = employeeService.increaseSalaryInRange(min, max, BigDecimal.valueOf(percentage));
            System.out.println("\nUpdated " + rowsAffected + " payroll record(s).");
        } catch (SQLException ex) {
            System.out.println("Database error: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void printEmployeeDetails(Employee e) {
        System.out.println("\n========================================");
        System.out.println("EMPLOYEE INFORMATION");
        System.out.println("========================================");
        System.out.println("Employee ID: " + e.getEmployeeId());
        System.out.println("Name: " + e.getFirstName() + " " + e.getLastName());
        System.out.println("SSN: " + e.getSsn());
        System.out.println("Email: " + e.getEmail());
        
        if (e.getDivisionId() != null) {
            String divisionName = getDivisionName(e.getDivisionId());
            System.out.println("Division: " + (divisionName != null ? divisionName : "ID: " + e.getDivisionId()));
        }
        
        if (e.getJobTitleId() != null) {
            String jobTitleName = getJobTitleName(e.getJobTitleId());
            System.out.println("Job Title: " + (jobTitleName != null ? jobTitleName : "ID: " + e.getJobTitleId()));
        }
        
        System.out.println("========================================");
    }

    private String getDivisionName(Integer divisionId) {
        if (divisionId == null) {
            return null;
        }
        String sql = "SELECT name FROM division WHERE division_id = ?";
        DatabaseConnectionManager dbManager = DatabaseConnectionManager.getInstance();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, divisionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException ex) {
            return null;
        }
        return null;
    }

    private String getJobTitleName(Integer jobTitleId) {
        if (jobTitleId == null) {
            return null;
        }
        String sql = "SELECT title FROM job_titles WHERE job_title_id = ?";
        DatabaseConnectionManager dbManager = DatabaseConnectionManager.getInstance();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobTitleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("title");
                }
            }
        } catch (SQLException ex) {
            return null;
        }
        return null;
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

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
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
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("This field is required.");
        }
    }

    private String readOptional(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
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
