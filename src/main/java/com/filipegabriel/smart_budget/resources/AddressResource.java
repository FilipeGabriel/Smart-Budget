package com.filipegabriel.smart_budget.resources;

import com.filipegabriel.smart_budget.entities.Address;
import com.filipegabriel.smart_budget.resources.dto.AddressDTO;
import com.filipegabriel.smart_budget.services.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/api/addresses")
public class AddressResource {

    @Autowired
    private AddressService service;

    @GetMapping("/{id}")
    public ResponseEntity<Address> findById(@PathVariable Long id) {
        Address address = service.getById(id);
        return ResponseEntity.ok().body(address);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Address>> findAllByClientId(@PathVariable Long clientId) {
        List<Address> addresses = service.findAllByClientId(clientId);
        return ResponseEntity.ok().body(addresses);
    }

    @PostMapping
    public ResponseEntity<Address> insert(@RequestBody AddressDTO addressDTO) {
        Address newAddress = service.insert(addressDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newAddress.getAddressId()).toUri();
        return ResponseEntity.created(uri).body(newAddress);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Address> update(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        Address updatedAddress = service.update(id, addressDTO);
        return ResponseEntity.ok().body(updatedAddress);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
