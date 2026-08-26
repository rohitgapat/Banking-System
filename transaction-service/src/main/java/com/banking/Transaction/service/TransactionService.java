package com.banking.Transaction.service;

import com.banking.Transaction.model.TransactionRequest;
import com.banking.Transaction.model.TransactionResponseDTO;

import java.util.List;

public interface TransactionService {

    TransactionResponseDTO createTransaction(TransactionRequest request);

    TransactionResponseDTO getTransactionById(Long id);

    List<TransactionResponseDTO> getAllTransactions();

    List<TransactionResponseDTO> getTransactionsByAccount(String accountNumber);
}