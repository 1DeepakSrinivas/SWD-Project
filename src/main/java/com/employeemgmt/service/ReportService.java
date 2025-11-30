package com.employeemgmt.service;

import com.employeemgmt.dao.*;
import com.employeemgmt.model.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {

    private final EmployeeDAO employeeDAO;
    private final DivisionDAO divisionDAO;
    private final JobTitleDAO jobTitleDAO;
    private final PayrollDAO payrollDAO;
    private final EmployeeDivisionDAO employeeDivisionDAO;
    private final EmployeeJobTitleDAO employeeJobTitleDAO;

    public ReportService(EmployeeDAO employeeDAO,
                         DivisionDAO divisionDAO,
                         JobTitleDAO jobTitleDAO,
                         PayrollDAO payrollDAO,
                         EmployeeDivisionDAO employeeDivisionDAO,
                         EmployeeJobTitleDAO employeeJobTitleDAO) {
        this.employeeDAO = employeeDAO;
        this.divisionDAO = divisionDAO;
        this.jobTitleDAO = jobTitleDAO;
        this.payrollDAO = payrollDAO;
        this.employeeDivisionDAO = employeeDivisionDAO;
        this.employeeJobTitleDAO = employeeJobTitleDAO;
    }

    // --------------------------------------------------------------------
    // 1) Full-time employee info + pay history
    // --------------------------------------------------------------------

    public Optional<Employee> getEmployee(int employeeId) throws SQLException {
        // Your EmployeeDAO returns Optional<Employee>
        return employeeDAO.findById(employeeId);
    }

    public List<Payroll> getPayHistoryForEmployee(int employeeId) throws SQLException {
        // Your PayrollDAO likely has this signature
        return payrollDAO.findByEmployeeId(employeeId);
    }

    // --------------------------------------------------------------------
    // Helpers: filter payrolls for a specific month
    // --------------------------------------------------------------------

    private List<Payroll> getPayrollsForMonth(int year, int month) throws SQLException {
        List<Payroll> all = payrollDAO.findAll();

        // Assuming Payroll has getPayPeriodEnd() or getPayPeriodStart() returning LocalDate.
        // If your getters are named differently or return java.sql.Date, tweak this lambda.
        return all.stream()
                .filter(p -> {
                    LocalDate date = p.getPayPeriodEnd(); // or getPayPeriodStart()
                    return date != null
                            && date.getYear() == year
                            && date.getMonthValue() == month;
                })
                .collect(Collectors.toList());
    }

    // --------------------------------------------------------------------
    // 2) Total pay by Job Title (for a month)
    // --------------------------------------------------------------------

    public Map<String, BigDecimal> getTotalPayByJobTitle(int year, int month) throws SQLException {
        List<Payroll> payrolls = getPayrollsForMonth(year, month);

        // empId -> jobTitleId from EmployeeJobTitle
        Map<Integer, Integer> empToJobTitleId = employeeJobTitleDAO.findAll()
                .stream()
                .collect(Collectors.toMap(
                        EmployeeJobTitle::getEmployeeId,
                        EmployeeJobTitle::getJobTitleId
                ));

        // jobTitleId -> JobTitle
        Map<Integer, JobTitle> jobTitleMap = jobTitleDAO.findAll()
                .stream()
                .collect(Collectors.toMap(JobTitle::getJobTitleId, jt -> jt));

        Map<String, BigDecimal> totals = new HashMap<>();

        for (Payroll p : payrolls) {
            int empId = p.getEmployeeId();
            Integer jobTitleId = empToJobTitleId.get(empId);
            if (jobTitleId == null) {
                continue;
            }

            JobTitle jobTitle = jobTitleMap.get(jobTitleId);
            if (jobTitle == null) {
                continue;
            }

            String titleName = jobTitle.getTitle();
            totals.merge(titleName, p.getAmount(), BigDecimal::add);
        }

        return totals;
    }

    // --------------------------------------------------------------------
    // 3) Total pay by Division (for a month)
    // --------------------------------------------------------------------

    public Map<String, BigDecimal> getTotalPayByDivision(int year, int month) throws SQLException {
        List<Payroll> payrolls = getPayrollsForMonth(year, month);

        // empId -> divisionId from EmployeeDivision
        Map<Integer, Integer> empToDivisionId = employeeDivisionDAO.findAll()
                .stream()
                .collect(Collectors.toMap(
                        EmployeeDivision::getEmployeeId,
                        EmployeeDivision::getDivisionId
                ));

        // divisionId -> Division
        Map<Integer, Division> divisionMap = divisionDAO.findAll()
                .stream()
                .collect(Collectors.toMap(Division::getDivisionId, d -> d));

        Map<String, BigDecimal> totals = new HashMap<>();

        for (Payroll p : payrolls) {
            int empId = p.getEmployeeId();
            Integer divisionId = empToDivisionId.get(empId);
            if (divisionId == null) {
                continue;
            }

            Division division = divisionMap.get(divisionId);
            if (division == null) {
                continue;
            }

            String divisionName = division.getName();
            totals.merge(divisionName, p.getAmount(), BigDecimal::add);
        }

        return totals;
    }
}
