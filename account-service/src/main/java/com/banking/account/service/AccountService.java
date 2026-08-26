package com.banking.account.service;

import java.util.List;

import com.banking.account.entity.Account;
import com.banking.account.entity.AccountStatus;
import com.banking.account.model.CreateAccountRequest;

public interface AccountService {

	Account createAccount(CreateAccountRequest request);

	Account getAccountById(Long id);

	Account getAccountByAccountNumber(String accountNumber);

	List<Account> getAllAccounts();

	Account deposit(String accountNumber, Double amount);

	Account withdraw(String accountNumber, Double amount);

	Double getBalance(String accountNumber);

	Account updateAccountStatus(String accountNumber,AccountStatus status);

	void closeAccount(String accountNumber);

	void deleteAccount(String accountNumber);
}
