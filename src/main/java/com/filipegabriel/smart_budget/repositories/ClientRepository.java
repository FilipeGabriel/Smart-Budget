package com.filipegabriel.smart_budget.repositories;

import com.filipegabriel.smart_budget.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
