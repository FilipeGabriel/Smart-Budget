package com.filipegabriel.smart_budget.services;

import com.filipegabriel.smart_budget.entities.Account;
import com.filipegabriel.smart_budget.entities.Bank;
import com.filipegabriel.smart_budget.entities.Client;
import com.filipegabriel.smart_budget.repositories.AccountRepository;
import com.filipegabriel.smart_budget.repositories.BankRepository;
import com.filipegabriel.smart_budget.repositories.ClientRepository;
import com.filipegabriel.smart_budget.repositories.MovementRepository;
import com.filipegabriel.smart_budget.resources.dto.AccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository repository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private MovementRepository movementRepository;

    @Autowired
    private BankRepository bankRepository;

    public Account getById(Long id) {
        Optional<Account> account = repository.findById(id);
        return account.get();
    }

    public List<Account> findAllByClientId(Long clientId) {
        return repository.findByClientClientId(clientId);
    }

    public List<Account> findAllActiveAccounts() {
        return repository.findByActiveTrue();
    }

    @Transactional
    public Account insert(AccountDTO accountDTO) {

        Client client = clientRepository.findById(accountDTO.getClientId()).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        Optional<Account> existing = repository.findByAccountNumber(accountDTO.getAccountNumber());
        if (existing.isPresent()) {
            return existing.get();
        }

        Account account = new Account();
        account.setAccountNumber(accountDTO.getAccountNumber());
        account.setBalance(accountDTO.getBalance());
        account.setCreationDate(LocalDate.now());
        account.setActive(accountDTO.getActive());
        account.setClient(client);
        client.getAccounts().add(account);

        Bank bank;
        if (bankRepository.existsByCode(accountDTO.getBankCode())) {
            bank = bankRepository.findByCode(accountDTO.getBankCode());
        } else {
            bank = new Bank();
            bank.setCode(accountDTO.getBankCode());
            bank.setName(accountDTO.getBankName());
        }

        account.setBank(bank);
        bank.getAccounts().add(account);

        bankRepository.save(bank);
        return repository.save(account);
    }

    public Account updateStatusAccount(Long id) {
        Account account = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Conta não encontrada com o ID: " + id));

        account.setActive(!account.getActive());

        return repository.save(account);
    }

    @Transactional
    public Account updateAccount(Long accountId, AccountDTO updatedDataDTO) {

        Account account = repository.findById(accountId).orElseThrow(() -> new EntityNotFoundException("Conta não encontrada: " + accountId));

        boolean hasMovements = movementRepository.existsByAccountAccountId(accountId);
        if (hasMovements) {
            throw new IllegalStateException("Não é permitido alterar contas que possuem movimentações associadas.");
        }

        account.setAccountNumber(updatedDataDTO.getAccountNumber());
        account.setCreationDate(datePattern(updatedDataDTO.getCreationDate()));

        Bank bank = bankRepository.findById(updatedDataDTO.getBankId()).orElseThrow(() -> new EntityNotFoundException("Banco não encontrado: " + updatedDataDTO.getBankId()));

        account.setBank(bank);

        return repository.save(account);
    }

    public LocalDate datePattern(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

}
