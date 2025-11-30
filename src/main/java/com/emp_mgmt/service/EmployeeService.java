package com.emp_mgmt.service;

import com.emp_mgmt.dao.EmployeeDAO;
import com.emp_mgmt.dao.PayrollDAO;
import com.emp_mgmt.model.Employee;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class EmployeeService {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final PayrollDAO payrollDAO = new PayrollDAO();

    public Employee addEmployee(Employee e, int divisionId, int jobTitleId) throws SQLException {
        return employeeDAO.insert(e, divisionId, jobTitleId);
    }

    public Optional<Employee> findById(int id) throws SQLException {
        return employeeDAO.findById(id);
    }

    public Optional<Employee> findBySSN(String ssn) throws SQLException {
        return employeeDAO.findBySSN(ssn);
    }

    public List<Employee> findByName(String fragment) throws SQLException {
        return employeeDAO.findByNameFragment(fragment);
    }

    public boolean updateEmployee(Employee e, int divisionId, int jobTitleId) throws SQLException {
        return employeeDAO.update(e, divisionId, jobTitleId);
    }

    public boolean deleteEmployee(int employeeId) throws SQLException {
        return employeeDAO.delete(employeeId);
    }

    /**
     * Increases payroll "salary" amounts in the given range by the given percentage.
     * Example: percentage 3.2 → +3.2%
     */
    public int increaseSalaryInRange(BigDecimal min, BigDecimal max, BigDecimal percentage) throws SQLException {
        return payrollDAO.increaseAmountInRange(min, max, percentage);
    }
}
