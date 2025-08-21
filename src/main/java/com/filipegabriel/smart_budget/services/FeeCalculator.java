package com.filipegabriel.smart_budget.services;

import com.filipegabriel.smart_budget.entities.Movement;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class FeeCalculator {
    public BigDecimal calculateRevenue(List<Movement> movements, LocalDate startDate) {

        LocalDate endDate = startDate.plusDays(30);
        long count = movements.stream()
                .filter(m -> !m.getMovementDate().isBefore(startDate) && !m.getMovementDate().isAfter(endDate))
                .count();

        BigDecimal feePerMovement;
        if (count <= 10) {
            feePerMovement = BigDecimal.valueOf(1.00);
        } else if (count <= 20) {
            feePerMovement = BigDecimal.valueOf(0.75);
        } else {
            feePerMovement = BigDecimal.valueOf(0.50);
        }

        return feePerMovement.multiply(BigDecimal.valueOf(count));

    }
//    public BigDecimal calculateFee(Movement movement, Client client) {
//        int totalMovements = client.getAccounts()
//                .stream()
//                .flatMap(a -> a.getMovements().stream())
//                .toArray().length;
//
//        if (totalMovements <= 10) {
//            return BigDecimal.valueOf(1.0);
//        } else if (totalMovements <= 20) {
//            return BigDecimal.valueOf(0.75);
//        } else {
//            return BigDecimal.valueOf(0.50);
//        }
//    }
}
