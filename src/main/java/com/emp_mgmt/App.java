package com.emp_mgmt;

import com.emp_mgmt.service.EmployeeService;
import com.emp_mgmt.service.ReportService;
import com.emp_mgmt.ui.ConsoleUI;
import com.emp_mgmt.ui.EmployeeConsole;
import com.emp_mgmt.ui.ReportConsole;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        EmployeeService employeeService = new EmployeeService();
        ReportService reportService = new ReportService();

        EmployeeConsole employeeConsole = new EmployeeConsole(scanner, employeeService);
        ReportConsole reportConsole = new ReportConsole(scanner, reportService, employeeService);

        ConsoleUI consoleUI = new ConsoleUI(scanner, employeeConsole, reportConsole);
        consoleUI.start();

        scanner.close();
        System.out.println("Exiting Employee Management System. Goodbye.");
    }
}
