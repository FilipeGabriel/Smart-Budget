package com.filipegabriel.smart_budget.services;

import com.filipegabriel.smart_budget.entities.*;
import com.filipegabriel.smart_budget.repositories.*;
import com.filipegabriel.smart_budget.resources.dto.NewClientDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private MovementRepository movementRepository;

    @Mock
    private BankRepository bankRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private ClientService clientService;

    private NewClientDTO clientDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        clientDTO = new NewClientDTO();
        clientDTO.setClientName("Extreme");
        clientDTO.setClientCpfCnpj("123456789");
        clientDTO.setClientEmail("extreme@gmail.com");
        clientDTO.setClientTelephone("81984946720");
        clientDTO.setClientType("PJ");
        clientDTO.setAccountNumber("12345-5");
        clientDTO.setAccountCreationDate("18/04/2002");
        clientDTO.setMovementAmount(BigDecimal.valueOf(50));
        clientDTO.setMovementType("DEPOSIT");
        clientDTO.setMovementDescription("Abertura de conta");
        clientDTO.setBankName("Caixa");
        clientDTO.setBankCode("103");
        clientDTO.setAddressStreet("via principal");
        clientDTO.setAddressNumber("76");
        clientDTO.setAddressComplement("apartamento");
        clientDTO.setAddressNeighborhood("sucupira");
        clientDTO.setAddressCity("Jaboatão");
        clientDTO.setAddressState("Pernambuco");
        clientDTO.setAddressZipCode("54280200");
    }

    @Test
    void testInsertClient_NewBank() {
        when(bankRepository.existsByCode("103")).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArguments()[0]);
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);
        when(movementRepository.save(any(Movement.class))).thenAnswer(i -> i.getArguments()[0]);
        when(addressRepository.save(any(Address.class))).thenAnswer(i -> i.getArguments()[0]);
        when(bankRepository.save(any(Bank.class))).thenAnswer(i -> i.getArguments()[0]);

        Client savedClient = clientService.insert(clientDTO);

        assertNotNull(savedClient);
        assertEquals("Extreme", savedClient.getName());
        assertEquals(1, savedClient.getAccounts().size());
        assertEquals(1, savedClient.getAddresses().size());
        assertEquals(BigDecimal.valueOf(50), savedClient.getAccounts().get(0).getBalance());

        // Verifica que o banco foi salvo
        verify(bankRepository, times(1)).save(any(Bank.class));
    }

    @Test
    void testUpdateClient() {
        Client client = new Client();
        client.setClientId(1L);
        client.setName("OldName");
        client.setEmail("old@gmail.com");
        client.setTelephone("123456");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArguments()[0]);

        clientService.update(1L, clientDTO);

        assertEquals("Extreme", client.getName());
        assertEquals("extreme@gmail.com", client.getEmail());
        assertEquals("81984946720", client.getTelephone());
    }

    @Test
    void testUpdateStatusClient() {
        Client client = new Client();
        client.setClientId(1L);
        client.setActive(true);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArguments()[0]);

        Client updated = clientService.updateStatusClient(1L);

        assertFalse(updated.getActive());
    }

    @Test
    void testGetById() {
        Client client = new Client();
        client.setClientId(1L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        Client found = clientService.getById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getClientId());
    }

    @Test
    void testFindAllActiveClients() {
        Client client1 = new Client();
        client1.setActive(true);
        Client client2 = new Client();
        client2.setActive(true);

        when(clientRepository.findByActiveTrue()).thenReturn(Arrays.asList(client1, client2));

        List<Client> result = clientService.findAllActiveClients();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(Client::getActive));
    }

}
