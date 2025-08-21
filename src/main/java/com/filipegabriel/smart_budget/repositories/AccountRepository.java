package com.filipegabriel.smart_budget.repositories;

import com.filipegabriel.smart_budget.entities.Account;
import com.filipegabriel.smart_budget.entities.Address;
import com.filipegabriel.smart_budget.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByClientClientId(Long clientId);

    List<Account> findByClientClientIdAndActiveTrue(Long clientId);

    List<Account> findByActiveTrue();

}
