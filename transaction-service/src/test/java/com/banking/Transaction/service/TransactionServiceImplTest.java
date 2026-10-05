package com.banking.Transaction.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.banking.Transaction.entity.Transaction;
import com.banking.Transaction.enums.TransactionStatus;
import com.banking.Transaction.enums.TransactionType;
import com.banking.Transaction.exception.TransferFailedException;
import com.banking.Transaction.model.AccountResponse;
import com.banking.Transaction.model.TransactionRequest;
import com.banking.Transaction.model.TransactionResponseDTO;
import com.banking.Transaction.model.TransferRequest;
import com.banking.Transaction.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountServiceCircuitBreaker accountServiceCircuitBreaker;

    @InjectMocks
    private TransactionServiceImpl transactionService;
    
    @Test
    void shouldCreateDepositTransactionSuccessfully() {

        TransactionRequest request = TransactionRequest.builder()
                .accountNumber("54765152133")
                .amount(500.0)
                .transactionType(TransactionType.DEPOSIT)
                .build();

        AccountResponse accountResponse = AccountResponse.builder()
                .accountNumber("54765152133")
                .balance(1500.0)
                .build();

        when(accountServiceCircuitBreaker.deposit(
                "54765152133",
                500.0
        )).thenReturn(accountResponse);

        Transaction transaction = Transaction.builder()
                .id(1L)
                .accountNumber("54765152133")
                .amount(500.0)
                .transactionType(TransactionType.DEPOSIT)
                .transactionDate(LocalDateTime.now())
                .status(TransactionStatus.SUCCESS)
                .build();

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(transaction);

        TransactionResponseDTO response =
                transactionService.createTransaction(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("54765152133", response.getAccountNumber());
        assertEquals(500.0, response.getAmount());
        assertEquals(
                TransactionType.DEPOSIT,
                response.getTransactionType()
        );
        assertEquals(
                TransactionStatus.SUCCESS,
                response.getStatus()
        );

        verify(accountServiceCircuitBreaker)
                .deposit("54765152133", 500.0);

        verify(transactionRepository)
                .save(any(Transaction.class));
    }
    
    @Test
    void shouldCreateWithdrawTransactionSuccessfully() {

        TransactionRequest request = TransactionRequest.builder()
                .accountNumber("54765152133")
                .amount(300.0)
                .transactionType(TransactionType.WITHDRAW)
                .build();

        AccountResponse accountResponse = AccountResponse.builder()
                .accountNumber("54765152133")
                .balance(1200.0)
                .build();

        when(accountServiceCircuitBreaker.withdraw(
                "54765152133",
                300.0
        )).thenReturn(accountResponse);

        Transaction transaction = Transaction.builder()
                .id(2L)
                .accountNumber("54765152133")
                .amount(300.0)
                .transactionType(TransactionType.WITHDRAW)
                .transactionDate(LocalDateTime.now())
                .status(TransactionStatus.SUCCESS)
                .build();

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(transaction);

        TransactionResponseDTO response =
                transactionService.createTransaction(request);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("54765152133", response.getAccountNumber());
        assertEquals(300.0, response.getAmount());

        assertEquals(
                TransactionType.WITHDRAW,
                response.getTransactionType()
        );

        assertEquals(
                TransactionStatus.SUCCESS,
                response.getStatus()
        );

        verify(accountServiceCircuitBreaker)
                .withdraw("54765152133", 300.0);

        verify(transactionRepository)
                .save(any(Transaction.class));
    }
    
    @Test
    void shouldTransferMoneySuccessfully() {

        TransferRequest request = TransferRequest.builder()
                .fromAccountNumber("54765152133")
                .toAccountNumber("98765432109")
                .amount(500.0)
                .build();

        // Mock sender withdrawal
        AccountResponse senderResponse = AccountResponse.builder()
                .accountNumber("54765152133")
                .balance(1000.0)
                .build();

        when(accountServiceCircuitBreaker.withdraw(
                "54765152133",
                500.0
        )).thenReturn(senderResponse);

        // Mock receiver deposit
        AccountResponse receiverResponse = AccountResponse.builder()
                .accountNumber("98765432109")
                .balance(1500.0)
                .build();

        when(accountServiceCircuitBreaker.deposit(
                "98765432109",
                500.0
        )).thenReturn(receiverResponse);

        // Transaction that repository will return
        Transaction transaction = Transaction.builder()
                .id(3L)
                .accountNumber("54765152133")
                .fromAccountNumber("54765152133")
                .toAccountNumber("98765432109")
                .amount(500.0)
                .transactionType(TransactionType.TRANSFER)
                .transactionDate(LocalDateTime.now())
                .status(TransactionStatus.SUCCESS)
                .build();

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(transaction);

        // Call actual service method
        TransactionResponseDTO response =
                transactionService.transfer(request);

        // Verify response
        assertNotNull(response);
        assertEquals(3L, response.getId());
        assertEquals("54765152133", response.getFromAccountNumber());
        assertEquals("98765432109", response.getToAccountNumber());
        assertEquals(500.0, response.getAmount());

        assertEquals(
                TransactionType.TRANSFER,
                response.getTransactionType()
        );

        assertEquals(
                TransactionStatus.SUCCESS,
                response.getStatus()
        );

        // Verify sender was debited
        verify(accountServiceCircuitBreaker)
                .withdraw("54765152133", 500.0);

        // Verify receiver was credited
        verify(accountServiceCircuitBreaker)
                .deposit("98765432109", 500.0);

        // Verify transaction was saved
        verify(transactionRepository)
                .save(any(Transaction.class));
    }
    
    @Test
    void shouldRollbackWhenReceiverDepositFails() {

        TransferRequest request = TransferRequest.builder()
                .fromAccountNumber("54765152133")
                .toAccountNumber("98765432109")
                .amount(500.0)
                .build();

        // 1. Sender withdrawal succeeds
        when(accountServiceCircuitBreaker.withdraw(
                "54765152133",
                500.0
        )).thenReturn(
                AccountResponse.builder()
                        .accountNumber("54765152133")
                        .balance(1000.0)
                        .build()
        );

        // 2. Receiver deposit fails
        when(accountServiceCircuitBreaker.deposit(
                "98765432109",
                500.0
        )).thenThrow(
                new RuntimeException("Receiver account service failed")
        );

        // 3. Sender rollback succeeds
        when(accountServiceCircuitBreaker.deposit(
                "54765152133",
                500.0
        )).thenReturn(
                AccountResponse.builder()
                        .accountNumber("54765152133")
                        .balance(1500.0)
                        .build()
        );

        // 4. Call transfer and expect exception
        assertThrows(
                TransferFailedException.class,
                () -> transactionService.transfer(request)
        );

        // 5. Verify original withdrawal happened
        verify(accountServiceCircuitBreaker)
                .withdraw("54765152133", 500.0);

        // 6. Verify receiver deposit was attempted
        verify(accountServiceCircuitBreaker)
                .deposit("98765432109", 500.0);

        // 7. Verify sender rollback happened
        verify(accountServiceCircuitBreaker)
                .deposit("54765152133", 500.0);

        // 8. Verify FAILED transaction was saved
        verify(transactionRepository)
                .save(any(Transaction.class));
    }
    
    @Test
    void shouldFailTransferWhenSenderWithdrawalFails() {

        TransferRequest request = TransferRequest.builder()
                .fromAccountNumber("54765152133")
                .toAccountNumber("98765432109")
                .amount(500.0)
                .build();

        // Sender withdrawal fails
        when(accountServiceCircuitBreaker.withdraw(
                "54765152133",
                500.0
        )).thenThrow(
                new RuntimeException("Sender account service failed")
        );

        // Transfer should fail
        assertThrows(
                TransferFailedException.class,
                () -> transactionService.transfer(request)
        );

        // Verify sender withdrawal was attempted
        verify(accountServiceCircuitBreaker)
                .withdraw("54765152133", 500.0);

        // Receiver should NOT be credited
        verify(accountServiceCircuitBreaker, never())
                .deposit("98765432109", 500.0);

        // Sender rollback should NOT happen
        verify(accountServiceCircuitBreaker, never())
                .deposit("54765152133", 500.0);

        // FAILED transaction should be saved
        verify(transactionRepository)
                .save(any(Transaction.class));
    }
}