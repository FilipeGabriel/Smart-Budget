package com.filipegabriel.smart_budget.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FeeCalculator {

    public BigDecimal calculateFee(int movementsCount) {
        if (movementsCount <= 10) {
            return BigDecimal.valueOf(1.0).multiply(BigDecimal.valueOf(movementsCount));
        } else if (movementsCount <= 20) {
            return BigDecimal.valueOf(0.75).multiply(BigDecimal.valueOf(movementsCount));
        } else {
            return BigDecimal.valueOf(0.5).multiply(BigDecimal.valueOf(movementsCount));
        }
    }
}