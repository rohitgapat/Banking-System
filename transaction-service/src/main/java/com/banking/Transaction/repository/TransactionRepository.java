package com.banking.Transaction.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banking.Transaction.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	
    List<Transaction> findByAccountNumber(String accountNumber);

}