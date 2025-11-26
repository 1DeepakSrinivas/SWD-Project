package com.emp_mgmt.dto;

import java.math.BigDecimal;

public class DivisionPay {
    private String divisionName;
    private BigDecimal totalPay;

    public DivisionPay(String divisionName, BigDecimal totalPay) {
        this.divisionName = divisionName;
        this.totalPay = totalPay;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public BigDecimal getTotalPay() {
        return totalPay;
    }
}
