package com.emp_mgmt.dto;

import com.emp_mgmt.model.Employee;
import com.emp_mgmt.model.PayrollRecord;

import java.util.ArrayList;
import java.util.List;

public class EmployeeWithPayHistory {
    private Employee employee;
    private String divisionName;
    private String jobTitleName;
    private List<PayrollRecord> payRecords = new ArrayList<>();

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    public String getJobTitleName() {
        return jobTitleName;
    }

    public void setJobTitleName(String jobTitleName) {
        this.jobTitleName = jobTitleName;
    }

    public List<PayrollRecord> getPayRecords() {
        return payRecords;
    }

    public void setPayRecords(List<PayrollRecord> payRecords) {
        this.payRecords = payRecords;
    }
}
