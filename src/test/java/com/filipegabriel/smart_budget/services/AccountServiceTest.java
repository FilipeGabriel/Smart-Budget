package com.filipegabriel.smart_budget.services;

import com.filipegabriel.smart_budget.entities.Account;
import com.filipegabriel.smart_budget.entities.Bank;
import com.filipegabriel.smart_budget.entities.Client;
import com.filipegabriel.smart_budget.repositories.AccountRepository;
import com.filipegabriel.smart_budget.repositories.BankRepository;
import com.filipegabriel.smart_budget.repositories.ClientRepository;
import com.filipegabriel.smart_budget.repositories.MovementRepository;
import com.filipegabriel.smart_budget.resources.dto.AccountDTO;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    @Mock
    private AccountRepository repository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private BankRepository bankRepository;

    @Mock
    private MovementRepository movementRepository;

    @InjectMocks
    private AccountService service;

    private Client client;
    private Bank bank;
    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        client = new Client();
        client.setClientId(1L);
        client.setName("Extreme");

        bank = new Bank();
        bank.setBankId(1L);
        bank.setCode("103");
        bank.setName("Banco XPTO");

        account = new Account();
        account.setAccountId(1L);
        account.setAccountNumber("00012345");
        account.setBalance(BigDecimal.valueOf(105));
        account.setCreationDate(LocalDate.of(2025, 8, 21));
        account.setActive(true);
        account.setClient(client);
        account.setBank(bank);

        client.getAccounts().add(account);
        bank.getAccounts().add(account);
    }

    @Test
    void getById_shouldReturnAccount() {
        when(repository.findById(1L)).thenReturn(Optional.of(account));

        Account result = service.getById(1L);

        assertNotNull(result);
        assertEquals("00012345", result.getAccountNumber());
    }

    @Test
    void findAllByClientId_shouldReturnAccounts() {
        when(repository.findByClientClientId(1L)).thenReturn(Arrays.asList(account));

        var result = service.findAllByClientId(1L);

        assertEquals(1, result.size());
        assertEquals(BigDecimal.valueOf(105), result.get(0).getBalance());
    }

    @Test
    void findAllActiveAccounts_shouldReturnAccounts() {
        when(repository.findByActiveTrue()).thenReturn(Arrays.asList(account));

        var result = service.findAllActiveAccounts();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getActive());
    }

    @Test
    void insert_shouldSaveAccountIfNotExists() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(repository.findByAccountNumber("00012345")).thenReturn(Optional.empty());
        when(bankRepository.existsByCode("103")).thenReturn(true);
        when(bankRepository.findByCode("103")).thenReturn(bank);
        when(bankRepository.save(bank)).thenReturn(bank);
        when(repository.save(any(Account.class))).thenReturn(account);

        Account result = service.insert(new AccountDTO(
                "12345678901",
                "00012345",
                "21/08/2025",
                BigDecimal.valueOf(105),
                true,
                "103",
                "Banco XPTO",
                1L,
                1L
        ));

        assertNotNull(result);
        assertEquals("00012345", result.getAccountNumber());
        verify(repository, times(1)).save(any(Account.class));
    }

    @Test
    void updateStatusAccount_shouldToggleActive() {
        when(repository.findById(1L)).thenReturn(Optional.of(account));
        when(repository.save(account)).thenReturn(account);

        Account result = service.updateStatusAccount(1L);

        assertFalse(result.getActive());
    }

    @Test
    void updateAccount_shouldUpdateIfNoMovements() {
        when(repository.findById(1L)).thenReturn(Optional.of(account));
        when(movementRepository.existsByAccountAccountId(1L)).thenReturn(false);
        when(bankRepository.findById(1L)).thenReturn(Optional.of(bank));
        when(repository.save(account)).thenReturn(account);

        AccountDTO dto = new AccountDTO(
                "12345678901",
                "00012345",
                "21/08/2025",
                BigDecimal.valueOf(105),
                true,
                "103",
                "Banco XPTO",
                1L,
                1L
        );

        Account result = service.updateAccount(1L, dto);

        assertNotNull(result);
        assertEquals("00012345", result.getAccountNumber());
    }

    @Test
    void updateAccount_shouldThrowIfHasMovements() {
        when(repository.findById(1L)).thenReturn(Optional.of(account));
        when(movementRepository.existsByAccountAccountId(1L)).thenReturn(true);

        AccountDTO dto = new AccountDTO();

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.updateAccount(1L, dto));
        assertEquals("Não é permitido alterar contas que possuem movimentações associadas.", ex.getMessage());
    }

}
