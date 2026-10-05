package com.banking.Transaction.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccountResponse {

    private Long id;
    private String accountNumber;
    private String accountType;
    private Double balance;
    private String status;
    private LocalDateTime createdAt;
}