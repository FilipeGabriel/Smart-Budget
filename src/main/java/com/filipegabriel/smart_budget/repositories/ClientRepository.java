package com.filipegabriel.smart_budget.repositories;

import com.filipegabriel.smart_budget.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByCpfCnpj(String cpfCnpj);

    List<Client> findByActiveTrue();

}
