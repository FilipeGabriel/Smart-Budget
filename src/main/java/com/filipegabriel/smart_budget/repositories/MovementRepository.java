package com.filipegabriel.smart_budget.repositories;

import com.filipegabriel.smart_budget.entities.Movement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovementRepository extends JpaRepository<Movement, Long> {

    List<Movement> findByAccountAccountId(Long accountId);

    boolean existsByAccountAccountId(Long accountId);

}
