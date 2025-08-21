package com.filipegabriel.smart_budget.services;

import com.filipegabriel.smart_budget.entities.Account;
import com.filipegabriel.smart_budget.entities.Movement;
import com.filipegabriel.smart_budget.entities.enums.MovementType;
import com.filipegabriel.smart_budget.repositories.AccountRepository;
import com.filipegabriel.smart_budget.repositories.MovementRepository;
import com.filipegabriel.smart_budget.resources.dto.MovementDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class MovementService {

    @Autowired
    private MovementRepository movementRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<Movement> findAllByAccountId(Long accountId) {
        return movementRepository.findByAccountAccountId(accountId);
    }

    public Movement getById(Long movementId) {
        return movementRepository.findById(movementId).orElseThrow(() -> new IllegalArgumentException("Movimento não encontrado: " + movementId));
    }

    @Transactional
    public Movement insert(MovementDTO movementDTO) {

        Account account = accountRepository.findById(movementDTO.getAccountId()).orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

        if (movementDTO.getAmount().compareTo(BigDecimal.ONE) < 0) {
            throw new IllegalArgumentException("O valor do movimento deve ser maior ou igual a 1");
        }

        Movement movement = new Movement();
        movement.setAmount(movementDTO.getAmount());
        movement.setDescription(movementDTO.getDescription());
        movement.setMovementDate(LocalDate.now());
        movement.setMovementType(MovementType.valueOf(movementDTO.getMovementType().toUpperCase()));
        movement.setAccount(account);

        switch (movement.getMovementType()) {
            case DEPOSIT:
                account.setBalance(account.getBalance().add(movementDTO.getAmount()));
                break;
            case WITHDRAW:
            case TRANSFER:
                if (account.getBalance().compareTo(movementDTO.getAmount()) < 0) {
                    throw new IllegalArgumentException("Saldo insuficiente para realizar o saque/transferência");
                }
                account.setBalance(account.getBalance().subtract(movementDTO.getAmount()));
                break;
        }

        accountRepository.save(account);
        return movementRepository.save(movement);
    }

}
