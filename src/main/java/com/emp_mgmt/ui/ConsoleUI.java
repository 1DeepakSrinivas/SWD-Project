package com.emp_mgmt.ui;

import com.emp_mgmt.service.EmployeeService;
import com.emp_mgmt.service.ReportService;

import java.util.Scanner;

public class ConsoleUI {

    private final Scanner scanner;
    private final EmployeeConsole employeeConsole;
    private final ReportConsole reportConsole;

    public ConsoleUI(Scanner scanner, EmployeeService employeeService, ReportService reportService) {
        this.scanner = scanner;
        this.employeeConsole = new EmployeeConsole(scanner, employeeService);
        this.reportConsole = new ReportConsole(scanner, reportService, employeeService);
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1 -> employeeConsole.searchEmployee();
                case 2 -> employeeConsole.updateEmployee();
                case 3 -> employeeConsole.updateSalaryByPercentage();
                case 4 -> reportConsole.showMenu();
                case 0 -> running = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n===== MAIN MENU =====");
        System.out.println("1. Search Employee");
        System.out.println("2. Update Employee");
        System.out.println("3. Update Salary by Percentage");
        System.out.println("4. Reports");
        System.out.println("0. Exit");
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
