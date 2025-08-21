package com.filipegabriel.smart_budget.repositories;

import com.filipegabriel.smart_budget.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
