package com.emp_mgmt.ui;

import com.employeemgmt.dao.DivisionDAO;
import com.employeemgmt.dao.DivisionDAOImpl;
import com.employeemgmt.dao.EmployeeDAO;
import com.employeemgmt.dao.EmployeeDAOImpl;
import com.employeemgmt.dao.EmployeeDivisionDAO;
import com.employeemgmt.dao.EmployeeDivisionDAOImpl;
import com.employeemgmt.dao.EmployeeJobTitleDAO;
import com.employeemgmt.dao.EmployeeJobTitleDAOImpl;
import com.employeemgmt.dao.JobTitleDAO;
import com.employeemgmt.dao.JobTitleDAOImpl;
import com.employeemgmt.dao.PayrollDAO;
import com.employeemgmt.dao.PayrollDAOImpl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class DAOConsole {

    private final Scanner scanner;
    private final EmployeeDAO employeeDAO;
    private final PayrollDAO payrollDAO;
    private final DivisionDAO divisionDAO;
    private final JobTitleDAO jobTitleDAO;
    private final EmployeeDivisionDAO employeeDivisionDAO;
    private final EmployeeJobTitleDAO employeeJobTitleDAO;

    public DAOConsole(Scanner scanner) {
        this.scanner = scanner;
        this.employeeDAO = new EmployeeDAOImpl();
        this.payrollDAO = new PayrollDAOImpl();
        this.divisionDAO = new DivisionDAOImpl();
        this.jobTitleDAO = new JobTitleDAOImpl();
        this.employeeDivisionDAO = new EmployeeDivisionDAOImpl();
        this.employeeJobTitleDAO = new EmployeeJobTitleDAOImpl();
    }

    public void showMenu() {
        boolean back = false;
        while (!back) {
            printDAOMenu();
            int choice = readInt("Choose an option: ");
            try {
                switch (choice) {
                    case 1 -> handleEmployeeDAOOperations();
                    case 2 -> handlePayrollDAOOperations();
                    case 3 -> handleDivisionDAOOperations();
                    case 4 -> handleJobTitleDAOOperations();
                    case 5 -> handleEmployeeDivisionDAOOperations();
                    case 6 -> handleEmployeeJobTitleDAOOperations();
                    case 7 -> handleSalaryUpdate();
                    case 0 -> back = true;
                    default -> System.out.println("Invalid choice. Try again.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void printDAOMenu() {
        System.out.println("\n----- DATA ACCESS MANAGEMENT MENU -----");
        System.out.println("1. Employee DAO Operations");
        System.out.println("2. Payroll DAO Operations");
        System.out.println("3. Division DAO Operations");
        System.out.println("4. Job Title DAO Operations");
        System.out.println("5. Employee-Division Relationship Operations");
        System.out.println("6. Employee-JobTitle Relationship Operations");
        System.out.println("7. Update Salary by Percentage (Transactional)");
        System.out.println("0. Back to Main Menu");
    }

    private void handleEmployeeDAOOperations() throws SQLException {
        System.out.println("\n--- Employee DAO Operations ---");
        System.out.println("1. Insert Employee");
        System.out.println("2. Find by ID");
        System.out.println("3. Find by SSN");
        System.out.println("4. Search by Name");
        System.out.println("5. Find All");
        System.out.println("6. Update Employee");
        System.out.println("7. Delete Employee");
        int choice = readInt("Choose: ");

        switch (choice) {
            case 1 -> {
                System.out.print("First Name: ");
                String firstName = scanner.nextLine();
                System.out.print("Last Name: ");
                String lastName = scanner.nextLine();
                System.out.print("SSN (9 digits): ");
                String ssn = scanner.nextLine();
                System.out.print("Email: ");
                String email = scanner.nextLine();
                com.employeemgmt.model.Employee emp = new com.employeemgmt.model.Employee(firstName, lastName, ssn, email);
                emp = employeeDAO.insert(emp);
                System.out.println("Inserted: " + emp);
            }
            case 2 -> {
                int id = readInt("Employee ID: ");
                employeeDAO.findById(id).ifPresentOrElse(
                    System.out::println,
                    () -> System.out.println("Not found")
                );
            }
            case 3 -> {
                System.out.print("SSN: ");
                String ssn = scanner.nextLine();
                employeeDAO.findBySSN(ssn).ifPresentOrElse(
                    System.out::println,
                    () -> System.out.println("Not found")
                );
            }
            case 4 -> {
                System.out.print("Name fragment: ");
                String fragment = scanner.nextLine();
                employeeDAO.searchByName(fragment).forEach(System.out::println);
            }
            case 5 -> employeeDAO.findAll().forEach(System.out::println);
            case 6 -> {
                int id = readInt("Employee ID: ");
                employeeDAO.findById(id).ifPresent(emp -> {
                    System.out.print("New Email: ");
                    String email = scanner.nextLine();
                    emp.setEmail(email);
                    try {
                        boolean updated = employeeDAO.update(emp);
                        System.out.println(updated ? "Updated" : "Update failed");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                });
            }
            case 7 -> {
                int id = readInt("Employee ID to delete: ");
                boolean deleted = employeeDAO.delete(id);
                System.out.println(deleted ? "Deleted" : "Delete failed");
            }
        }
    }

    private void handlePayrollDAOOperations() throws SQLException {
        System.out.println("\n--- Payroll DAO Operations ---");
        System.out.println("1. Insert Payroll");
        System.out.println("2. Find by ID");
        System.out.println("3. Find by Employee ID");
        System.out.println("4. Find All");
        System.out.println("5. Update Payroll");
        System.out.println("6. Delete Payroll");
        int choice = readInt("Choose: ");

        switch (choice) {
            case 1 -> {
                int empId = readInt("Employee ID: ");
                System.out.print("Amount: ");
                BigDecimal amount = new BigDecimal(scanner.nextLine());
                System.out.print("Period Start (YYYY-MM-DD): ");
                LocalDate start = LocalDate.parse(scanner.nextLine());
                System.out.print("Period End (YYYY-MM-DD): ");
                LocalDate end = LocalDate.parse(scanner.nextLine());
                com.employeemgmt.model.Payroll payroll = new com.employeemgmt.model.Payroll(empId, amount, start, end);
                payroll = payrollDAO.insert(payroll);
                System.out.println("Inserted: " + payroll);
            }
            case 2 -> {
                int id = readInt("Payroll ID: ");
                payrollDAO.findById(id).ifPresentOrElse(
                    System.out::println,
                    () -> System.out.println("Not found")
                );
            }
            case 3 -> {
                int empId = readInt("Employee ID: ");
                payrollDAO.findByEmployeeId(empId).forEach(System.out::println);
            }
            case 4 -> payrollDAO.findAll().forEach(System.out::println);
            case 5 -> {
                int id = readInt("Payroll ID: ");
                payrollDAO.findById(id).ifPresent(payroll -> {
                    System.out.print("New Amount: ");
                    payroll.setAmount(new BigDecimal(scanner.nextLine()));
                    try {
                        boolean updated = payrollDAO.update(payroll);
                        System.out.println(updated ? "Updated" : "Update failed");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                });
            }
            case 6 -> {
                int id = readInt("Payroll ID to delete: ");
                boolean deleted = payrollDAO.delete(id);
                System.out.println(deleted ? "Deleted" : "Delete failed");
            }
        }
    }

    private void handleDivisionDAOOperations() throws SQLException {
        System.out.println("\n--- Division DAO Operations ---");
        System.out.println("1. Insert Division");
        System.out.println("2. Find by ID");
        System.out.println("3. Find All");
        System.out.println("4. Update Division");
        System.out.println("5. Delete Division");
        int choice = readInt("Choose: ");

        switch (choice) {
            case 1 -> {
                System.out.print("Division Name: ");
                String name = scanner.nextLine();
                com.employeemgmt.model.Division div = new com.employeemgmt.model.Division(name);
                div = divisionDAO.insert(div);
                System.out.println("Inserted: " + div);
            }
            case 2 -> {
                int id = readInt("Division ID: ");
                divisionDAO.findById(id).ifPresentOrElse(
                    System.out::println,
                    () -> System.out.println("Not found")
                );
            }
            case 3 -> divisionDAO.findAll().forEach(System.out::println);
            case 4 -> {
                int id = readInt("Division ID: ");
                divisionDAO.findById(id).ifPresent(div -> {
                    System.out.print("New Name: ");
                    div.setName(scanner.nextLine());
                    try {
                        boolean updated = divisionDAO.update(div);
                        System.out.println(updated ? "Updated" : "Update failed");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                });
            }
            case 5 -> {
                int id = readInt("Division ID to delete: ");
                boolean deleted = divisionDAO.delete(id);
                System.out.println(deleted ? "Deleted" : "Delete failed");
            }
        }
    }

    private void handleJobTitleDAOOperations() throws SQLException {
        System.out.println("\n--- Job Title DAO Operations ---");
        System.out.println("1. Insert Job Title");
        System.out.println("2. Find by ID");
        System.out.println("3. Find All");
        System.out.println("4. Update Job Title");
        System.out.println("5. Delete Job Title");
        int choice = readInt("Choose: ");

        switch (choice) {
            case 1 -> {
                System.out.print("Job Title: ");
                String title = scanner.nextLine();
                com.employeemgmt.model.JobTitle jobTitle = new com.employeemgmt.model.JobTitle(title);
                jobTitle = jobTitleDAO.insert(jobTitle);
                System.out.println("Inserted: " + jobTitle);
            }
            case 2 -> {
                int id = readInt("Job Title ID: ");
                jobTitleDAO.findById(id).ifPresentOrElse(
                    System.out::println,
                    () -> System.out.println("Not found")
                );
            }
            case 3 -> jobTitleDAO.findAll().forEach(System.out::println);
            case 4 -> {
                int id = readInt("Job Title ID: ");
                jobTitleDAO.findById(id).ifPresent(jobTitle -> {
                    System.out.print("New Title: ");
                    jobTitle.setTitle(scanner.nextLine());
                    try {
                        boolean updated = jobTitleDAO.update(jobTitle);
                        System.out.println(updated ? "Updated" : "Update failed");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                });
            }
            case 5 -> {
                int id = readInt("Job Title ID to delete: ");
                boolean deleted = jobTitleDAO.delete(id);
                System.out.println(deleted ? "Deleted" : "Delete failed");
            }
        }
    }

    private void handleEmployeeDivisionDAOOperations() throws SQLException {
        System.out.println("\n--- Employee-Division Operations ---");
        System.out.println("1. Insert Relationship");
        System.out.println("2. Find by IDs");
        System.out.println("3. Find All");
        System.out.println("4. Find by Employee ID");
        System.out.println("5. Find by Division ID");
        System.out.println("6. Delete Relationship");
        int choice = readInt("Choose: ");

        switch (choice) {
            case 1 -> {
                int empId = readInt("Employee ID: ");
                int divId = readInt("Division ID: ");
                com.employeemgmt.model.EmployeeDivision ed = new com.employeemgmt.model.EmployeeDivision(empId, divId);
                employeeDivisionDAO.insert(ed);
                System.out.println("Inserted: " + ed);
            }
            case 2 -> {
                int empId = readInt("Employee ID: ");
                int divId = readInt("Division ID: ");
                employeeDivisionDAO.findById(empId, divId).ifPresentOrElse(
                    System.out::println,
                    () -> System.out.println("Not found")
                );
            }
            case 3 -> employeeDivisionDAO.findAll().forEach(System.out::println);
            case 4 -> {
                int empId = readInt("Employee ID: ");
                employeeDivisionDAO.findByEmployeeId(empId).forEach(System.out::println);
            }
            case 5 -> {
                int divId = readInt("Division ID: ");
                employeeDivisionDAO.findByDivisionId(divId).forEach(System.out::println);
            }
            case 6 -> {
                int empId = readInt("Employee ID: ");
                int divId = readInt("Division ID: ");
                boolean deleted = employeeDivisionDAO.delete(empId, divId);
                System.out.println(deleted ? "Deleted" : "Delete failed");
            }
        }
    }

    private void handleEmployeeJobTitleDAOOperations() throws SQLException {
        System.out.println("\n--- Employee-JobTitle Operations ---");
        System.out.println("1. Insert Relationship");
        System.out.println("2. Find by IDs");
        System.out.println("3. Find All");
        System.out.println("4. Find by Employee ID");
        System.out.println("5. Find by Job Title ID");
        System.out.println("6. Delete Relationship");
        int choice = readInt("Choose: ");

        switch (choice) {
            case 1 -> {
                int empId = readInt("Employee ID: ");
                int jobTitleId = readInt("Job Title ID: ");
                com.employeemgmt.model.EmployeeJobTitle ejt = new com.employeemgmt.model.EmployeeJobTitle(empId, jobTitleId);
                employeeJobTitleDAO.insert(ejt);
                System.out.println("Inserted: " + ejt);
            }
            case 2 -> {
                int empId = readInt("Employee ID: ");
                int jobTitleId = readInt("Job Title ID: ");
                employeeJobTitleDAO.findById(empId, jobTitleId).ifPresentOrElse(
                    System.out::println,
                    () -> System.out.println("Not found")
                );
            }
            case 3 -> employeeJobTitleDAO.findAll().forEach(System.out::println);
            case 4 -> {
                int empId = readInt("Employee ID: ");
                employeeJobTitleDAO.findByEmployeeId(empId).forEach(System.out::println);
            }
            case 5 -> {
                int jobTitleId = readInt("Job Title ID: ");
                employeeJobTitleDAO.findByJobTitleId(jobTitleId).forEach(System.out::println);
            }
            case 6 -> {
                int empId = readInt("Employee ID: ");
                int jobTitleId = readInt("Job Title ID: ");
                boolean deleted = employeeJobTitleDAO.delete(empId, jobTitleId);
                System.out.println(deleted ? "Deleted" : "Delete failed");
            }
        }
    }

    private void handleSalaryUpdate() throws SQLException {
        System.out.println("\n--- Update Salary by Percentage (Transactional) ---");
        System.out.print("Percentage (e.g., 5.0 for 5%): ");
        double percentage = Double.parseDouble(scanner.nextLine());
        System.out.print("Minimum amount: ");
        BigDecimal min = new BigDecimal(scanner.nextLine());
        System.out.print("Maximum amount: ");
        BigDecimal max = new BigDecimal(scanner.nextLine());

        int rowsAffected = employeeDAO.updateSalaryByPercentage(percentage, min, max);
        System.out.println("Updated " + rowsAffected + " payroll record(s).");
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }
}

