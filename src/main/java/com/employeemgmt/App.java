package com.employeemgmt;

import com.employeemgmt.dao.*;
import com.employeemgmt.service.EmployeeService;
import com.employeemgmt.service.ReportService;
import com.employeemgmt.ui.ConsoleUI;
import com.employeemgmt.ui.EmployeeConsole;
import com.employeemgmt.ui.ReportConsole;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        EmployeeDAO employeeDAO = new EmployeeDAOImpl();
        DivisionDAO divisionDAO = new DivisionDAOImpl();
        JobTitleDAO jobTitleDAO = new JobTitleDAOImpl();
        PayrollDAO payrollDAO = new PayrollDAOImpl();
        EmployeeDivisionDAO employeeDivisionDAO = new EmployeeDivisionDAOImpl();
        EmployeeJobTitleDAO employeeJobTitleDAO = new EmployeeJobTitleDAOImpl();

        // Services to wrap the DAOs
        EmployeeService employeeService = new EmployeeService(
                employeeDAO,
                divisionDAO,
                jobTitleDAO
        );

        ReportService reportService = new ReportService(
                employeeDAO,
                divisionDAO,
                jobTitleDAO,
                payrollDAO,
                employeeDivisionDAO,
                employeeJobTitleDAO
        );

        // Console UIs
        EmployeeConsole employeeConsole = new EmployeeConsole(scanner, employeeService);
        ReportConsole reportConsole = new ReportConsole(scanner, reportService);

        ConsoleUI consoleUI = new ConsoleUI(scanner, employeeConsole, reportConsole);
        consoleUI.start();

        scanner.close();
        System.out.println("Exiting Employee Management System. Goodbye.");
    }
}
