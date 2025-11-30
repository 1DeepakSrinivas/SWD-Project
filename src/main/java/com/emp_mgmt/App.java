package com.emp_mgmt;

import com.emp_mgmt.service.EmployeeService;
import com.emp_mgmt.service.ReportService;
import com.emp_mgmt.ui.ConsoleUI;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        EmployeeService employeeService = new EmployeeService();
        ReportService reportService = new ReportService();

        ConsoleUI console = new ConsoleUI(scanner, employeeService, reportService);

        System.out.println("========================================");
        System.out.println("  EMPLOYEE MANAGEMENT SYSTEM");
        System.out.println("========================================\n");

        console.start();

        scanner.close();
        System.out.println("\nExiting Employee Management System. Goodbye.");
    }
}
