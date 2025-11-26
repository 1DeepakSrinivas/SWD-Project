package com.emp_mgmt.dto;

import java.math.BigDecimal;

public class JobTitlePay {
    private String jobTitleName;
    private BigDecimal totalPay;

    public JobTitlePay(String jobTitleName, BigDecimal totalPay) {
        this.jobTitleName = jobTitleName;
        this.totalPay = totalPay;
    }

    public String getJobTitleName() {
        return jobTitleName;
    }

    public BigDecimal getTotalPay() {
        return totalPay;
    }
}
