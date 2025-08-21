package com.filipegabriel.smart_budget.resources.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementDTO {

    private BigDecimal amount;

    private String movementType;

    private String description;

    private Long accountId;

}
