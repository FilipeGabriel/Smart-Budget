package com.filipegabriel.smart_budget.services;

import com.filipegabriel.smart_budget.entities.Client;
import com.filipegabriel.smart_budget.entities.Movement;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FeeCalculator {
    public BigDecimal calculateFee(Movement movement, Client client) {
        int totalMovements = client.getAccounts()
                .stream()
                .flatMap(a -> a.getMovements().stream())
                .toArray().length;

        if (totalMovements <= 10) {
            return BigDecimal.valueOf(1.0);
        } else if (totalMovements <= 20) {
            return BigDecimal.valueOf(0.75);
        } else {
            return BigDecimal.valueOf(0.50);
        }
    }
}
