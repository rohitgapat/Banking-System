package com.banking.Transaction.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banking.Transaction.client.AccountClient;
import com.banking.Transaction.model.AccountResponse;
import com.banking.Transaction.model.TransactionRequest;
import com.banking.Transaction.model.TransactionResponseDTO;
import com.banking.Transaction.model.TransferRequest;
import com.banking.Transaction.service.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountClient accountClient;
    
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @Valid @RequestBody TransactionRequest request) {

        TransactionResponseDTO response =
                transactionService.createTransaction(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public TransactionResponseDTO getTransactionById(
            @PathVariable Long id) {

        return transactionService.getTransactionById(id);
    }

    @GetMapping
    public List<TransactionResponseDTO> getAllTransactions() {

        return transactionService.getAllTransactions();
    }

    @GetMapping("/account/{accountNumber}")
    public List<TransactionResponseDTO> getTransactionsByAccount(
            @PathVariable String accountNumber) {

        return transactionService
                .getTransactionsByAccount(accountNumber);
    }

    @GetMapping("/account-info/{accountNumber}")
    public AccountResponse getAccount(
            @PathVariable String accountNumber) {

        return accountClient.getAccount(accountNumber);
    }
    
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transfer(
            @Valid @RequestBody TransferRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.transfer(request));
    }
}