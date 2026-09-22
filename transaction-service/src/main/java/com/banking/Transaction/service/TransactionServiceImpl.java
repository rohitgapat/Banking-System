package com.banking.Transaction.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.banking.Transaction.entity.Transaction;
import com.banking.Transaction.enums.TransactionStatus;
import com.banking.Transaction.enums.TransactionType;
import com.banking.Transaction.exception.TransferFailedException;
import com.banking.Transaction.model.AccountResponse;
import com.banking.Transaction.model.TransactionRequest;
import com.banking.Transaction.model.TransactionResponseDTO;
import com.banking.Transaction.model.TransferRequest;
import com.banking.Transaction.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountServiceCircuitBreaker accountServiceCircuitBreaker;

    @Override
    public TransactionResponseDTO createTransaction(TransactionRequest request) {

        // 1. Call account-service based on transaction type
        AccountResponse account;

        if (request.getTransactionType() == TransactionType.DEPOSIT) {

        	account = accountServiceCircuitBreaker.deposit(
        	        request.getAccountNumber(),
        	        request.getAmount());

        } else if (request.getTransactionType() == TransactionType.WITHDRAW) {

        	account = accountServiceCircuitBreaker.withdraw(
        	        request.getAccountNumber(),
        	        request.getAmount());

        } else {

            throw new RuntimeException("Invalid transaction type");
        }

        // 2. Create transaction object
        Transaction transaction = Transaction.builder()
                .accountNumber(request.getAccountNumber())
                .amount(request.getAmount())
                .transactionType(request.getTransactionType())
                .transactionDate(LocalDateTime.now())
                .status(TransactionStatus.SUCCESS)
                .build();

        // 3. Save transaction
        Transaction savedTransaction =
                transactionRepository.save(transaction);

        // 4. Convert Entity → DTO
        return convertToDTO(savedTransaction);
    }

    @Override
    public TransactionResponseDTO getTransactionById(Long id) {

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Transaction not found"));

        return convertToDTO(transaction);
    }

    @Override
    public List<TransactionResponseDTO> getAllTransactions() {

        return transactionRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<TransactionResponseDTO> getTransactionsByAccount(
            String accountNumber) {

        return transactionRepository
                .findByAccountNumber(accountNumber)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Override
    public TransactionResponseDTO transfer(TransferRequest request) {

        boolean senderDebited = false;
        boolean receiverCredited = false;

        try {

            // 1. Withdraw money from sender
            accountServiceCircuitBreaker.withdraw(
                    request.getFromAccountNumber(),
                    request.getAmount()
            );

            senderDebited = true;

            // 2. Deposit money into receiver
            accountServiceCircuitBreaker.deposit(
                    request.getToAccountNumber(),
                    request.getAmount()
            );

            receiverCredited = true;

            // 3. Save transfer transaction
            Transaction transaction = Transaction.builder()
                    .accountNumber(request.getFromAccountNumber())
                    .fromAccountNumber(request.getFromAccountNumber())
                    .toAccountNumber(request.getToAccountNumber())
                    .amount(request.getAmount())
                    .transactionType(TransactionType.TRANSFER)
                    .transactionDate(LocalDateTime.now())
                    .status(TransactionStatus.SUCCESS)
                    .build();

            Transaction savedTransaction =
                    transactionRepository.save(transaction);

            return convertToDTO(savedTransaction);

        } 
        
        catch (Exception ex) {

            // Rollback receiver credit
            if (receiverCredited) {
                try {
                    accountServiceCircuitBreaker.withdraw(
                            request.getToAccountNumber(),
                            request.getAmount()
                    );
                } 
                
                catch (Exception rollbackEx) {
                    System.err.println(
                            "Receiver rollback failed: "
                            + rollbackEx.getMessage()
                    );
                }
            }

            // Rollback sender debit
            if (senderDebited) {
                try {
                    accountServiceCircuitBreaker.deposit(
                            request.getFromAccountNumber(),
                            request.getAmount()
                    );
                } 
                
                catch (Exception rollbackEx) {
                    System.err.println(
                            "Sender rollback failed: "
                            + rollbackEx.getMessage()
                    );
                }
            }

            throw new TransferFailedException(
                    "Money transfer failed and rollback was attempted",ex
            );
        }
    }

    // Common conversion method
    private TransactionResponseDTO convertToDTO(Transaction transaction) {

        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .accountNumber(transaction.getAccountNumber())
                .fromAccountNumber(transaction.getFromAccountNumber())
                .toAccountNumber(transaction.getToAccountNumber())
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType())
                .transactionDate(transaction.getTransactionDate())
                .status(transaction.getStatus())
                .build();
    }
}