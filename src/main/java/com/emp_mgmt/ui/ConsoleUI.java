package com.emp_mgmt.ui;

import java.util.Scanner;

public class ConsoleUI {

    private final Scanner scanner;
    private final EmployeeConsole employeeConsole;
    private final ReportConsole reportConsole;

    public ConsoleUI(Scanner scanner,
                     EmployeeConsole employeeConsole,
                     ReportConsole reportConsole) {
        this.scanner = scanner;
        this.employeeConsole = employeeConsole;
        this.reportConsole = reportConsole;
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1 -> employeeConsole.showMenu();
                case 2 -> reportConsole.showMenu();
                case 0 -> running = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n===== EMPLOYEE MANAGEMENT SYSTEM =====");
        System.out.println("1. Employee Management");
        System.out.println("2. Reports");
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
