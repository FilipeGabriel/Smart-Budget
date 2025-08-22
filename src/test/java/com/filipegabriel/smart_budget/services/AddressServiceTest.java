package com.filipegabriel.smart_budget.services;

import com.filipegabriel.smart_budget.entities.Address;
import com.filipegabriel.smart_budget.entities.Client;
import com.filipegabriel.smart_budget.repositories.AddressRepository;
import com.filipegabriel.smart_budget.resources.dto.AddressDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AddressServiceTest {

    @Mock
    private AddressRepository repository;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private AddressService service;

    private Client client;
    private Address address;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        client = new Client();
        client.setClientId(1L);
        client.setName("Extreme");

        address = new Address();
        address.setAddressId(1L);
        address.setStreet("rua pio XII");
        address.setStreetNumber("84");
        address.setComplement("casa");
        address.setNeighborhood("toto");
        address.setCity("Recife");
        address.setState("Pernambuco");
        address.setZipCode("50940240");
        address.setClient(client);

        client.getAddresses().add(address);
    }

    @Test
    void getById_shouldReturnAddress() {
        when(repository.findById(1L)).thenReturn(Optional.of(address));

        Address result = service.getById(1L);

        assertNotNull(result);
        assertEquals("rua pio XII", result.getStreet());
    }

    @Test
    void findAllByClientId_shouldReturnAddresses() {
        when(repository.findByClientClientId(1L)).thenReturn(Arrays.asList(address));

        List<Address> result = service.findAllByClientId(1L);

        assertEquals(1, result.size());
        assertEquals("Recife", result.get(0).getCity());
    }

    @Test
    void insert_shouldSaveAddress() {
        when(clientService.getById(1L)).thenReturn(client);
        when(repository.save(any(Address.class))).thenReturn(address);

        Address result = service.insert(new AddressDTO(
                "rua pio XII",
                "84",
                "casa",
                "toto",
                "Recife",
                "Pernambuco",
                "50940240",
                1L
        ));

        assertNotNull(result);
        assertEquals("toto", result.getNeighborhood());
        verify(repository, times(1)).save(any(Address.class));
    }

    @Test
    void update_shouldModifyAddress() {
        when(repository.findById(1L)).thenReturn(Optional.of(address));
        when(repository.save(any(Address.class))).thenReturn(address);

        AddressDTO dto = new AddressDTO(
                "rua nova",
                "100",
                "apartamento",
                "bairro novo",
                "Olinda",
                "Pernambuco",
                "50940000",
                1L
        );

        Address result = service.update(1L, dto);

        assertEquals("rua nova", result.getStreet());
        assertEquals("Olinda", result.getCity());
    }

    @Test
    void delete_shouldRemoveAddressIfMoreThanOne() {
        // Cria outro endereço para permitir deleção
        Address anotherAddress = new Address();
        anotherAddress.setAddressId(2L);
        client.getAddresses().add(anotherAddress);

        when(repository.findById(1L)).thenReturn(Optional.of(address));
        when(clientService.getById(1L)).thenReturn(client);

        service.delete(1L);

        verify(repository, times(1)).delete(address);
    }

    @Test
    void delete_shouldThrowExceptionIfOnlyOneAddress() {
        when(repository.findById(1L)).thenReturn(Optional.of(address));
        when(clientService.getById(1L)).thenReturn(client);

        // remove outro endereço se existir
        client.getAddresses().clear();
        client.getAddresses().add(address);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.delete(1L));
        assertEquals("O cliente deve ter pelo menos um endereço.", exception.getMessage());
    }

}
