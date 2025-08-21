package com.filipegabriel.smart_budget.resources;

import com.filipegabriel.smart_budget.entities.Account;
import com.filipegabriel.smart_budget.resources.dto.AccountDTO;
import com.filipegabriel.smart_budget.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/api/accounts")
public class AccountResource {

    @Autowired
    private AccountService service;

    @GetMapping("/{id}")
    public ResponseEntity<Account> findById(@PathVariable Long id) {
        Account account = service.getById(id);
        return ResponseEntity.ok().body(account);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Account>> findAllByClientId(@PathVariable Long clientId) {
        List<Account> accounts = service.findAllByClientId(clientId);
        return ResponseEntity.ok().body(accounts);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Account>> findAllActiveAccounts() {
        List<Account> accounts = service.findAllActiveAccounts();
        return ResponseEntity.ok().body(accounts);
    }

    @PostMapping("/external")
    public ResponseEntity<Account> createAccount(@RequestBody AccountDTO accountDTO) {
        Account account = service.insert(accountDTO);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/external/change-status/{id}")
    public ResponseEntity<Account> updateStatusAccount(@PathVariable Long id) {
        Account updateStatusAccount = service.updateStatusAccount(id);
        return ResponseEntity.ok().body(updateStatusAccount);
    }

    @PutMapping("/external/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody AccountDTO accountDTO) {
        Account updatedAccount = service.updateAccount(id, accountDTO);
        return ResponseEntity.ok().body(updatedAccount);
    }

}
