package com.banking.Transaction.exception;

public class InvalidTransactionTypeException extends RuntimeException {

    public InvalidTransactionTypeException(String message) {
        super(message);
    }
}