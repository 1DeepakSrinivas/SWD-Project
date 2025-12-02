package com.employeemgmt.service;

import com.employeemgmt.dao.DivisionDAO;
import com.employeemgmt.dao.EmployeeDAO;
import com.employeemgmt.dao.EmployeeDivisionDAO;
import com.employeemgmt.dao.EmployeeJobTitleDAO;
import com.employeemgmt.dao.JobTitleDAO;
import com.employeemgmt.model.Division;
import com.employeemgmt.model.Employee;
import com.employeemgmt.model.EmployeeDivision;
import com.employeemgmt.model.EmployeeJobTitle;
import com.employeemgmt.model.JobTitle;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class EmployeeService {

    private final EmployeeDAO employeeDAO;
    private final DivisionDAO divisionDAO;
    private final JobTitleDAO jobTitleDAO;
    private final EmployeeDivisionDAO employeeDivisionDAO;
    private final EmployeeJobTitleDAO employeeJobTitleDAO;

    public EmployeeService(EmployeeDAO employeeDAO,
                           DivisionDAO divisionDAO,
                           JobTitleDAO jobTitleDAO,
                           EmployeeDivisionDAO employeeDivisionDAO,
                           EmployeeJobTitleDAO employeeJobTitleDAO) {
        this.employeeDAO = employeeDAO;
        this.divisionDAO = divisionDAO;
        this.jobTitleDAO = jobTitleDAO;
        this.employeeDivisionDAO = employeeDivisionDAO;
        this.employeeJobTitleDAO = employeeJobTitleDAO;
    }

    // --- Create employee ---
    public Employee addEmployee(Employee employee, int divisionId, int jobTitleId) throws SQLException {
        Employee inserted = employeeDAO.insert(employee);

        if (inserted.getEmployeeId() == null) {
            throw new SQLException("Failed to obtain generated employee ID after insert");
        }

        EmployeeDivision newDivision = new EmployeeDivision(inserted.getEmployeeId(), divisionId);
        employeeDivisionDAO.insert(newDivision);

        EmployeeJobTitle newJobTitle = new EmployeeJobTitle(inserted.getEmployeeId(), jobTitleId);
        employeeJobTitleDAO.insert(newJobTitle);

        enrichEmployeeWithDivisionAndJobTitle(inserted);

        return inserted;
    }

    // --- Lookups ---

    public Optional<Employee> findById(int id) throws SQLException {
        Optional<Employee> employee = employeeDAO.findById(id);
        if (employee.isPresent()) {
            enrichEmployeeWithDivisionAndJobTitle(employee.get());
        }
        return employee;
    }

    public Optional<Employee> findBySSN(String ssn) throws SQLException {
        Optional<Employee> employee = employeeDAO.findBySSN(ssn);
        if (employee.isPresent()) {
            enrichEmployeeWithDivisionAndJobTitle(employee.get());
        }
        return employee;
    }

    public List<Employee> findByNameFragment(String fragment) throws SQLException {
        List<Employee> employees = employeeDAO.searchByName(fragment);
        for (Employee employee : employees) {
            enrichEmployeeWithDivisionAndJobTitle(employee);
        }
        return employees;
    }

    private void enrichEmployeeWithDivisionAndJobTitle(Employee employee) throws SQLException {
        if (employee.getEmployeeId() == null) {
            return;
        }

        List<EmployeeDivision> employeeDivisions = employeeDivisionDAO.findByEmployeeId(employee.getEmployeeId());
        if (!employeeDivisions.isEmpty()) {
            EmployeeDivision empDiv = employeeDivisions.get(0);
            Optional<Division> division = divisionDAO.findById(empDiv.getDivisionId());
            division.ifPresent(d -> employee.setDivisionName(d.getName()));
        }

        List<EmployeeJobTitle> employeeJobTitles = employeeJobTitleDAO.findByEmployeeId(employee.getEmployeeId());
        if (!employeeJobTitles.isEmpty()) {
            EmployeeJobTitle empJob = employeeJobTitles.get(0);
            Optional<JobTitle> jobTitle = jobTitleDAO.findById(empJob.getJobTitleId());
            jobTitle.ifPresent(jt -> employee.setJobTitleName(jt.getTitle()));
        }
    }

    // --- Update / delete ---

    public boolean updateEmployee(Employee employee, int divisionId, int jobTitleId) throws SQLException {
        if (employee.getEmployeeId() == null) {
            throw new IllegalArgumentException("Employee ID is required for update");
        }

        // Update employee basic info
        boolean updated = employeeDAO.update(employee);

        if (updated) {
            // Update division relationship
            List<EmployeeDivision> existingDivisions = employeeDivisionDAO.findByEmployeeId(employee.getEmployeeId());
            if (!existingDivisions.isEmpty()) {
                // Delete existing division relationship
                employeeDivisionDAO.delete(employee.getEmployeeId(), existingDivisions.get(0).getDivisionId());
            }
            // Insert new division relationship
            EmployeeDivision newDivision = new EmployeeDivision(employee.getEmployeeId(), divisionId);
            employeeDivisionDAO.insert(newDivision);

            // Update job title relationship
            List<EmployeeJobTitle> existingJobTitles = employeeJobTitleDAO.findByEmployeeId(employee.getEmployeeId());
            if (!existingJobTitles.isEmpty()) {
                // Delete existing job title relationship
                employeeJobTitleDAO.delete(employee.getEmployeeId(), existingJobTitles.get(0).getJobTitleId());
            }
            // Insert new job title relationship
            EmployeeJobTitle newJobTitle = new EmployeeJobTitle(employee.getEmployeeId(), jobTitleId);
            employeeJobTitleDAO.insert(newJobTitle);
        }

        return updated;
    }

    public boolean deleteEmployee(int employeeId) throws SQLException {
        return employeeDAO.delete(employeeId);
    }

    // --- Salary update in range ---

    public int increaseSalaryInRange(BigDecimal min, BigDecimal max, BigDecimal percentage) throws SQLException {
        return employeeDAO.updateSalaryByPercentage(percentage.doubleValue(), min, max);
    }

    // --- For UI dropdowns ---

    public List<Division> getAllDivisions() throws SQLException {
        return divisionDAO.findAll();
    }

    public List<JobTitle> getAllJobTitles() throws SQLException {
        return jobTitleDAO.findAll();
    }
}
