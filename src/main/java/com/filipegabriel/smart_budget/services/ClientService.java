package com.filipegabriel.smart_budget.services;

import com.filipegabriel.smart_budget.entities.*;
import com.filipegabriel.smart_budget.entities.enums.ClientType;
import com.filipegabriel.smart_budget.entities.enums.MovementType;
import com.filipegabriel.smart_budget.repositories.*;
import com.filipegabriel.smart_budget.resources.dto.NewClientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository repository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MovementRepository movementRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private AddressRepository addressRepository;

    public Client getById(Long id) {
        Optional<Client> client = repository.findById(id);
        return client.get();
    }

    public List<Client> findAllActiveClients() {
        return repository.findByActiveTrue();
    }

    public List<Client> findAll() {
        return repository.findAll();
    }

    @Transactional
    public Client insert(NewClientDTO clientDTO) {
        Client client = new Client();
        client.setName(clientDTO.getClientName());
        client.setCpfCnpj(clientDTO.getClientCpfCnpj());
        client.setEmail(clientDTO.getClientEmail());
        client.setTelephone(clientDTO.getClientTelephone());
        client.setActive(true);
        client.setRegistrationDate(LocalDate.now());

        try {
            client.setClientType(ClientType.valueOf(clientDTO.getClientType().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de cliente inválido: " + clientDTO.getClientType());
        }

        Account account = new Account();
        account.setAccountNumber(clientDTO.getAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        account.setCreationDate(datePattern(clientDTO.getAccountCreationDate()));
        account.setActive(true);
        account.setClient(client);
        client.getAccounts().add(account);

        BigDecimal amount = clientDTO.getMovementAmount();
        if (amount.compareTo(BigDecimal.ONE) < 0) {
            throw new IllegalArgumentException("O valor do movimento deve ser maior ou igual a 1");
        }

        Movement movement = new Movement();
        movement.setMovementDate(LocalDate.now());
        movement.setAmount(amount);
        try {
            movement.setMovementType(MovementType.valueOf(clientDTO.getMovementType().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de movimentação inválida: " + clientDTO.getMovementType());
        }
        movement.setDescription(clientDTO.getMovementDescription());
        account.setBalance(account.getBalance().add(amount));
        account.getMovements().add(movement);
        movement.setAccount(account);

        Bank bank;
        if (bankRepository.existsByCode(clientDTO.getBankCode())) {
            bank = bankRepository.findByCode(clientDTO.getBankCode());
        } else {
            bank = new Bank();
            bank.setName(clientDTO.getBankName());
            bank.setCode(clientDTO.getBankCode());
        }
        bank.getAccounts().add(account);
        account.setBank(bank);

        Address address = new Address();
        address.setStreet(clientDTO.getAddressStreet());
        address.setNumber(clientDTO.getAddressNumber());
        address.setComplement(clientDTO.getAddressComplement());
        address.setNeighborhood(clientDTO.getAddressNeighborhood());
        address.setCity(clientDTO.getAddressCity());
        address.setState(clientDTO.getAddressState());
        address.setZipCode(clientDTO.getAddressZipCode());
        address.setClient(client);
        client.getAddresses().add(address);

        bankRepository.save(bank);
        repository.save(client);
        accountRepository.save(account);
        movementRepository.save(movement);
        addressRepository.save(address);

        return client;
    }

    public LocalDate datePattern(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public Client update(Long id, NewClientDTO clientDTO) {
        Client client = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com o ID: " + id));

        client.setName(clientDTO.getClientName());
        client.setEmail(clientDTO.getClientEmail());
        client.setTelephone(clientDTO.getClientTelephone());

        return repository.save(client);
    }

    public Client updateStatusClient(Long id) {
        Client client = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com o ID: " + id));

        client.setActive(!client.getActive());

        return repository.save(client);
    }
}
