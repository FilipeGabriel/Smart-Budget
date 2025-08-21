package com.filipegabriel.smart_budget.repositories;

import com.filipegabriel.smart_budget.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
