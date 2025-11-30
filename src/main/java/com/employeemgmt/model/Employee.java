package com.employeemgmt.model;

import java.util.regex.Pattern;

public class Employee {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final int SSN_LENGTH = 9;

    private Integer employeeId;
    private String firstName;
    private String lastName;
    private String ssn;
    private String email;

    public Employee() {
    }

    public Employee(Integer employeeId, String firstName, String lastName, String ssn, String email) {
        this.setEmployeeId(employeeId);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setSsn(ssn);
        this.setEmail(email);
    }

    public Employee(String firstName, String lastName, String ssn, String email) {
        this(null, firstName, lastName, ssn, email);
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        if (employeeId != null && employeeId < 0) {
            throw new IllegalArgumentException("Employee ID must be non-negative");
        }
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        if (ssn != null && (ssn.length() != SSN_LENGTH || !ssn.matches("\\d{" + SSN_LENGTH + "}"))) {
            throw new IllegalArgumentException("SSN must be exactly 9 digits");
        }
        this.ssn = ssn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("Employee[ID: %d, Name: %s %s, SSN: %s, Email: %s]",
            employeeId != null ? employeeId : 0,
            firstName != null ? firstName : "",
            lastName != null ? lastName : "",
            ssn != null ? ssn : "",
            email != null ? email : ""
        );
    }
}

