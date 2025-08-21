package com.filipegabriel.smart_budget.resources;

import com.filipegabriel.smart_budget.entities.Client;
import com.filipegabriel.smart_budget.resources.dto.NewClientDTO;
import com.filipegabriel.smart_budget.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/api/clients")
public class ClientResource {

    @Autowired
    private ClientService service;

    @GetMapping("/{id}")
    public ResponseEntity<Client> findById(@PathVariable Long id) {
        Client client = service.getById(id);
        return ResponseEntity.ok().body(client);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Client>> findAllActiveClients() {
        List<Client> clients = service.findAllActiveClients();
        return ResponseEntity.ok().body(clients);
    }

    @GetMapping
    public ResponseEntity<List<Client>> findAll() {
        List<Client> clients = service.findAll();
        return ResponseEntity.ok().body(clients);
    }

    @PostMapping
    public ResponseEntity<Client> insert(@RequestBody NewClientDTO clientDTO) {
        Client newClient = service.insert(clientDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newClient.getClientId()).toUri();
        return ResponseEntity.created(uri).body(newClient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> update(@PathVariable Long id, @RequestBody NewClientDTO clientDTO) {
        Client updatedClient = service.update(id, clientDTO);
        return ResponseEntity.ok().body(updatedClient);
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity<Client> updateStatusClient(@PathVariable Long id) {
        Client updatedClient = service.updateStatusClient(id);
        return ResponseEntity.ok().body(updatedClient);
    }

}
