package com.emp_mgmt.service;

import com.emp_mgmt.dao.ReportDAO;
import com.emp_mgmt.dto.DivisionPay;
import com.emp_mgmt.dto.EmployeeWithPayHistory;
import com.emp_mgmt.dto.JobTitlePay;

import java.sql.SQLException;
import java.util.List;

public class ReportService {

    private final ReportDAO reportDAO = new ReportDAO();

    public EmployeeWithPayHistory getEmployeeWithPayHistory(int employeeId) throws SQLException {
        return reportDAO.getEmployeeWithPayHistory(employeeId);
    }

    public List<JobTitlePay> getTotalPayByJobTitle(int year, int month) throws SQLException {
        return reportDAO.getTotalPayByJobTitle(year, month);
    }

    public List<DivisionPay> getTotalPayByDivision(int year, int month) throws SQLException {
        return reportDAO.getTotalPayByDivision(year, month);
    }
}
