package com.banking.Transaction.service;

import org.springframework.stereotype.Service;

import com.banking.Transaction.client.AccountClient;
import com.banking.Transaction.exception.AccountOperationException;
import com.banking.Transaction.model.AccountResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountServiceCircuitBreaker {

    private final AccountClient accountClient;
    
    private static final Logger log =
            LoggerFactory.getLogger(AccountServiceCircuitBreaker.class);
    

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

        log.error("Deposit call to Account Service failed", ex);

        throw new AccountOperationException(
                "Account Service call failed: "
                + ex.getClass().getSimpleName()
                + " - "
                + ex.getMessage()
        );
    }

    public AccountResponse withdrawFallback(
            String accountNumber,
            Double amount,
            Throwable ex) {

        log.error("Withdraw call to Account Service failed", ex);

        throw new AccountOperationException(
                "Account Service call failed: "
                + ex.getClass().getSimpleName()
                + " - "
                + ex.getMessage()
        );
    }
}