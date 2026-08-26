package com.banking.account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banking.account.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
	
    Optional<Account> findByAccountNumber(String accountNumber);


    boolean existsByAccountNumber(String accountNumber);

}