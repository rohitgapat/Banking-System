package com.banking.Transaction.exception;

public class AccountOperationException extends RuntimeException {

    public AccountOperationException(String message) {
        super(message);
    }
}