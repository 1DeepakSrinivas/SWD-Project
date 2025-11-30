package com.emp_mgmt.dao;

import com.emp_mgmt.db.DatabaseConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PayrollDAO {

    private final DatabaseConnectionManager dbManager = DatabaseConnectionManager.getInstance();

    /**
     * Interprets "salary" as the payroll amount.
     * Increases payroll.amount by given percentage for rows whose amount is within [min, max].
     */
    public int increaseAmountInRange(BigDecimal min, BigDecimal max, BigDecimal percent) throws SQLException {
        if (percent == null) {
            throw new IllegalArgumentException("percent must not be null");
        }
        if (min == null) {
            throw new IllegalArgumentException("min must not be null");
        }
        if (max == null) {
            throw new IllegalArgumentException("max must not be null");
        }
        // percent 3.2 means +3.2%
        BigDecimal factor = percent.divide(BigDecimal.valueOf(100)).add(BigDecimal.ONE);

        String sql = """
            UPDATE payroll
            SET amount = amount * ?
            WHERE amount BETWEEN ? AND ?
            """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, factor);
            ps.setBigDecimal(2, min);
            ps.setBigDecimal(3, max);
            return ps.executeUpdate();
        }
    }
}
