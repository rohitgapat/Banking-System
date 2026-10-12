package com.banking.Transaction;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.banking.Transaction.enums.TransactionType;
import com.banking.Transaction.model.TransactionRequest;
import com.banking.Transaction.model.TransactionResponseDTO;
import com.banking.Transaction.repository.TransactionRepository;
import com.banking.Transaction.service.AccountServiceCircuitBreaker;
import com.banking.Transaction.service.TransactionService;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TransactionIntegrationTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @MockitoBean
    private AccountServiceCircuitBreaker accountServiceCircuitBreaker;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
    }
    
    @Test
    void createTransaction_shouldSaveTransactionToDatabase() {

        TransactionRequest request = new TransactionRequest();
        request.setAccountNumber("12345678901");
        request.setTransactionType(TransactionType.DEPOSIT);
        request.setAmount(500.0);

        TransactionResponseDTO response =
                transactionService.createTransaction(request);

        assertThat(response).isNotNull();

        assertThat(transactionRepository.count()).isEqualTo(1);

        var savedTransaction =
                transactionRepository.findAll().get(0);

        assertThat(savedTransaction.getAccountNumber())
                .isEqualTo("12345678901");

        assertThat(savedTransaction.getAmount())
                .isEqualTo(500.0);
    }
    
    @Test
    void getTransactionById_shouldReturnSavedTransaction() {
        // Arrange
        TransactionRequest request = new TransactionRequest();
        request.setAccountNumber("12345678901");
        request.setTransactionType(TransactionType.DEPOSIT);
        request.setAmount(500.0);

        TransactionResponseDTO created =
                transactionService.createTransaction(request);

        // Act
        TransactionResponseDTO result =
                transactionService.getTransactionById(created.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAccountNumber()).isEqualTo("12345678901");
        assertThat(result.getAmount()).isEqualTo(500.0);
    }
}