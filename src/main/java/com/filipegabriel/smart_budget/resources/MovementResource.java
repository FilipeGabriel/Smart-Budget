package com.filipegabriel.smart_budget.resources;

import com.filipegabriel.smart_budget.entities.Movement;
import com.filipegabriel.smart_budget.resources.dto.MovementDTO;
import com.filipegabriel.smart_budget.services.MovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/api/movements")
public class MovementResource {

    @Autowired
    private MovementService service;

    @GetMapping("/{id}")
    public ResponseEntity<Movement> findById(@PathVariable Long id) {
        Movement movement = service.getById(id);
        return ResponseEntity.ok().body(movement);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Movement>> findAllByAccountId(@PathVariable Long accountId) {
        List<Movement> movements = service.findAllByAccountId(accountId);
        return ResponseEntity.ok().body(movements);
    }

    @PostMapping("/external")
    public ResponseEntity<Movement> insert(@RequestBody MovementDTO movementDTO) {
        Movement movement = service.insert(movementDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(movement.getMovementId()).toUri();
        return ResponseEntity.created(uri).body(movement);
    }

}
