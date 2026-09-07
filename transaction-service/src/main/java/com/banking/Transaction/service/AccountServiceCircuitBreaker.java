package com.banking.Transaction.service;

import org.springframework.stereotype.Service;

import com.banking.Transaction.client.AccountClient;
import com.banking.Transaction.exception.AccountOperationException;
import com.banking.Transaction.model.AccountResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountServiceCircuitBreaker {

    private final AccountClient accountClient;
    

    @CircuitBreaker(
            name = "accountService",
            fallbackMethod = "depositFallback"
    )
    public AccountResponse deposit(String accountNumber, Double amount) {

        return accountClient.deposit(accountNumber, amount);
    }

    @CircuitBreaker(
            name = "accountService",
            fallbackMethod = "withdrawFallback"
    )
    public AccountResponse withdraw(String accountNumber, Double amount) {

        return accountClient.withdraw(accountNumber, amount);
    }

    public AccountResponse depositFallback(
            String accountNumber,
            Double amount,
            Throwable ex) {

        throw new AccountOperationException(
                "Account Service is currently unavailable. Please try again later."
        );
    }

    public AccountResponse withdrawFallback(
            String accountNumber,
            Double amount,
            Throwable ex) {

        throw new AccountOperationException(
                "Account Service is currently unavailable. Please try again later."
        );
    }
}