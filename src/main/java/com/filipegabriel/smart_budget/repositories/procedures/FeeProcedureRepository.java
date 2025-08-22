package com.filipegabriel.smart_budget.repositories.procedures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;

@Repository
public class FeeProcedureRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public BigDecimal calculateFee(int movementsCount) {
        return jdbcTemplate.execute((Connection con) -> {
            try (CallableStatement cs = con.prepareCall("{ call CALCULATE_FEE(?, ?) }")) {
                cs.setInt(1, movementsCount);
                cs.registerOutParameter(2, java.sql.Types.NUMERIC);
                cs.execute();
                return cs.getBigDecimal(2);
            }
        });
    }
}
