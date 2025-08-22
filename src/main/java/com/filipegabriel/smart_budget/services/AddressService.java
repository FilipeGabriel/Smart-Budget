package com.filipegabriel.smart_budget.services;

import com.filipegabriel.smart_budget.entities.Address;
import com.filipegabriel.smart_budget.entities.Client;
import com.filipegabriel.smart_budget.repositories.AddressRepository;
import com.filipegabriel.smart_budget.resources.dto.AddressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    @Autowired
    private AddressRepository repository;

    @Autowired
    private ClientService clientService;

    public Address getById(Long id) {
        Optional<Address> address = repository.findById(id);
        return address.get();
    }

    public List<Address> findAllByClientId(Long clientId) {
        return repository.findByClientClientId(clientId);
    }

    public Address insert(AddressDTO addressDTO) {
        Address address = new Address();
        Client client = clientService.getById(addressDTO.getClientId());

        address.setStreet(addressDTO.getStreet());
        address.setStreetNumber(addressDTO.getStreetNumber());
        address.setComplement(addressDTO.getComplement());
        address.setNeighborhood(addressDTO.getNeighborhood());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setZipCode(addressDTO.getZipCode());
        address.setClient(client);

        return repository.save(address);
    }

    public Address update(Long id, AddressDTO addressDTO) {
        Address address = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com o ID: " + id));
        upDateAddress(address, addressDTO);

        return repository.save(address);
    }

    public void upDateAddress(Address address, AddressDTO addressDTO) {
        address.setStreet(addressDTO.getStreet());
        address.setStreetNumber(addressDTO.getStreetNumber());
        address.setComplement(addressDTO.getComplement());
        address.setNeighborhood(addressDTO.getNeighborhood());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setZipCode(addressDTO.getZipCode());
    }

    public void delete(Long id) {
        Address address = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado com o ID: " + id));
        Client client = clientService.getById(address.getClient().getClientId());

        if (client.getAddresses().size() > 1) {
            repository.delete(address);
        } else {
            throw new IllegalArgumentException("O cliente deve ter pelo menos um endereço.");
        }
    }
}
