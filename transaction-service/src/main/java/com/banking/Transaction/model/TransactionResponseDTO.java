package com.banking.Transaction.model;

import java.time.LocalDateTime;

import com.banking.Transaction.enums.TransactionStatus;
import com.banking.Transaction.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDTO {

    private Long id;

    private String accountNumber;

    private Double amount;

    private TransactionType transactionType;

    private LocalDateTime transactionDate;

    private TransactionStatus status;
}