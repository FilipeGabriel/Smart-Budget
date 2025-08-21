package com.filipegabriel.smart_budget.resources.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {

    private String cpfCnpj;

    private String accountNumber;

    private String creationDate;

    private BigDecimal balance;

    private Boolean active;

    private String bankCode;

    private String bankName;

    private Long clientId;

    private Long bankId;

}
