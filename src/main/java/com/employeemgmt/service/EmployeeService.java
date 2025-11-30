package com.employeemgmt.service;

import com.employeemgmt.dao.DivisionDAO;
import com.employeemgmt.dao.EmployeeDAO;
import com.employeemgmt.dao.JobTitleDAO;
import com.employeemgmt.model.Division;
import com.employeemgmt.model.Employee;
import com.employeemgmt.model.JobTitle;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class EmployeeService {

    private final EmployeeDAO employeeDAO;
    private final DivisionDAO divisionDAO;
    private final JobTitleDAO jobTitleDAO;

    public EmployeeService(EmployeeDAO employeeDAO,
                           DivisionDAO divisionDAO,
                           JobTitleDAO jobTitleDAO) {
        this.employeeDAO = employeeDAO;
        this.divisionDAO = divisionDAO;
        this.jobTitleDAO = jobTitleDAO;
    }

    // --- Create employee ---
    public Employee addEmployee(Employee employee, int divisionId, int jobTitleId) throws SQLException {
        // For now we just insert the employee; mapping to division/job title can be added later
        return employeeDAO.insert(employee);
    }

    // --- Lookups ---

    public Optional<Employee> findById(int id) throws SQLException {
        // DAO already returns Optional<Employee>
        return employeeDAO.findById(id);
    }

    public Optional<Employee> findBySSN(String ssn) throws SQLException {
        return employeeDAO.findBySSN(ssn);
    }

    public List<Employee> findByNameFragment(String fragment) throws SQLException {
        return employeeDAO.searchByName(fragment);
    }

    // --- Update / delete ---

    public boolean updateEmployee(Employee employee, int divisionId, int jobTitleId) throws SQLException {
        // For now, just update core employee data
        return employeeDAO.update(employee);
    }

    public boolean deleteEmployee(int employeeId) throws SQLException {
        return employeeDAO.delete(employeeId);
    }

    // --- Salary update in range ---

    public int increaseSalaryInRange(BigDecimal min, BigDecimal max, BigDecimal percentage) throws SQLException {
        // DAO expects: (double percentage, BigDecimal min, BigDecimal max)
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
