package com.filipegabriel.smart_budget.repositories;

import com.filipegabriel.smart_budget.entities.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank, Long> {
}
