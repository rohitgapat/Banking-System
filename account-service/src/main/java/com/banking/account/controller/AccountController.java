package com.banking.account.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.banking.account.entity.Account;
import com.banking.account.entity.AccountStatus;
import com.banking.account.model.CreateAccountRequest;
import com.banking.account.service.AccountService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {

        Account account = accountService.createAccount(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                accountService.getAccountById(id)
        );
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<Account> getAccountByAccountNumber(
            @PathVariable String accountNumber) {

        return ResponseEntity.ok(
                accountService.getAccountByAccountNumber(accountNumber)
        );
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {

        return ResponseEntity.ok(
                accountService.getAllAccounts()
        );
    }

    @PatchMapping("/{accountNumber}/deposit")
    public ResponseEntity<Account> deposit(
            @PathVariable String accountNumber,
            @RequestParam Double amount) {

        return ResponseEntity.ok(
                accountService.deposit(accountNumber, amount)
        );
    }

    @PatchMapping("/{accountNumber}/withdraw")
    public ResponseEntity<Account> withdraw(
            @PathVariable String accountNumber,
            @RequestParam Double amount) {

        return ResponseEntity.ok(
                accountService.withdraw(accountNumber, amount)
        );
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<Double> getBalance(
            @PathVariable String accountNumber) {

        return ResponseEntity.ok(
                accountService.getBalance(accountNumber)
        );
    }

    @PatchMapping("/{accountNumber}/status")
    public ResponseEntity<Account> updateAccountStatus(
            @PathVariable String accountNumber,
            @RequestParam AccountStatus status) {

        return ResponseEntity.ok(
                accountService.updateAccountStatus(
                        accountNumber, status
                )
        );
    }

    @PatchMapping("/{accountNumber}/close")
    public ResponseEntity<Void> closeAccount(
            @PathVariable String accountNumber) {

        accountService.closeAccount(accountNumber);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable String accountNumber) {

        accountService.deleteAccount(accountNumber);

        return ResponseEntity.noContent().build();
    }
}