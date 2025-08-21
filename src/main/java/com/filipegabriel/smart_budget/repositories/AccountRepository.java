package com.filipegabriel.smart_budget.repositories;

import com.filipegabriel.smart_budget.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
