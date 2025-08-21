package com.filipegabriel.smart_budget.repositories;

import com.filipegabriel.smart_budget.entities.Movement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovementRepository extends JpaRepository<Movement, Long> {

    boolean existsByAccountAccountId(Long accountId);

}
