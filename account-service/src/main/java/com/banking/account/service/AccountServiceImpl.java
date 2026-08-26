package com.banking.account.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.banking.account.entity.Account;
import com.banking.account.entity.AccountStatus;
import com.banking.account.exception.AccountNotActiveException;
import com.banking.account.exception.AccountNotFoundException;
import com.banking.account.exception.InsufficientBalanceException;
import com.banking.account.model.CreateAccountRequest;
import com.banking.account.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Account createAccount(CreateAccountRequest request) {

        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .accountType(request.getAccountType())
                .balance(500.0)
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        return accountRepository.save(account);
    }

    @Override
    public Account getAccountById(Long id) {

        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Override
    public Account getAccountByAccountNumber(String accountNumber) {

        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    @Override
    public List<Account> getAllAccounts() {

        return accountRepository.findAll();
    }

    @Override
    public Account deposit(String accountNumber, Double amount) {

        Account account = getAccountByAccountNumber(accountNumber);

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountNotActiveException(
                    "Account is not active");
        }
        
        if (amount == null || amount <= 0) {
            throw new RuntimeException("Deposit amount must be greater than zero");
        }

        account.setBalance(account.getBalance() + amount);

        return accountRepository.save(account);
    }

    @Override
    public Account withdraw(String accountNumber, Double amount) {

        Account account = getAccountByAccountNumber(accountNumber);

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountNotActiveException(
                    "Account is not active");
        }

        if (amount == null || amount <= 0) {
            throw new RuntimeException("Withdrawal amount must be greater than zero");
        }

        if (account.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        account.setBalance(account.getBalance() - amount);

        return accountRepository.save(account);
    }

    @Override
    public Double getBalance(String accountNumber) {

        Account account = getAccountByAccountNumber(accountNumber);

        return account.getBalance();
    }

    @Override
    public Account updateAccountStatus(
            String accountNumber,
            AccountStatus status) {

        Account account = getAccountByAccountNumber(accountNumber);

        account.setStatus(status);

        return accountRepository.save(account);
    }

    @Override
    public void closeAccount(String accountNumber) {

        Account account = getAccountByAccountNumber(accountNumber);

        account.setStatus(AccountStatus.CLOSED);

        accountRepository.save(account);
    }

    @Override
    public void deleteAccount(String accountNumber) {

        Account account = getAccountByAccountNumber(accountNumber);

        accountRepository.delete(account);
    }

    private String generateAccountNumber() {

        String accountNumber;

        do {
            long number = 10_000_000_000L
                    + (long) (Math.random() * 90_000_000_000L);

            accountNumber = String.valueOf(number);

        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }
}