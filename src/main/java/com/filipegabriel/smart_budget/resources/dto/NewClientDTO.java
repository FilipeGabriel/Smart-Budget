package com.filipegabriel.smart_budget.resources.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewClientDTO {
// Client information

    private String clientName;

    private String clientCpfCnpj;

    private String clientEmail;

    private String clientTelephone;

    private String clientType;

// Account information

    private String accountNumber;

    private String accountCreationDate;

// Movement Information

    private BigDecimal movementAmount;

    private String movementType;

    private String movementDescription;

// Bank Information

    private String bankName;

    private String bankCode;

// Address Information

    private String addressStreet;

    private String addressNumber;

    private String addressComplement;

    private String addressNeighborhood;

    private String addressCity;

    private String addressState;

    private String addressZipCode;

}
