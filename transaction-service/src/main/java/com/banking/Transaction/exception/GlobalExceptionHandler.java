package com.banking.Transaction.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.banking.Transaction.model.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AccountOperationException.class)
	public ResponseEntity<String> handleAccountOperation(
	        AccountOperationException ex) {

	    return ResponseEntity
	            .status(HttpStatus.SERVICE_UNAVAILABLE)
	            .body(ex.getMessage());
	}
	
	@ExceptionHandler(TransferFailedException.class)
	public ResponseEntity<ErrorResponse> handleTransferFailed(
	        TransferFailedException ex) {

	    ErrorResponse errorResponse = ErrorResponse.builder()
	            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
	            .message(ex.getMessage())
	            .timestamp(LocalDateTime.now())
	            .build();

	    return ResponseEntity
	            .status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(errorResponse);
	}
	
	@ExceptionHandler(TransactionNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleTransactionNotFound(
	        TransactionNotFoundException ex) {

	    ErrorResponse errorResponse = ErrorResponse.builder()
	            .status(HttpStatus.NOT_FOUND.value())
	            .message(ex.getMessage())
	            .timestamp(LocalDateTime.now())
	            .build();

	    return ResponseEntity
	            .status(HttpStatus.NOT_FOUND)
	            .body(errorResponse);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationErrors(
	        MethodArgumentNotValidException ex) {

	    String message = ex.getBindingResult()
	            .getFieldErrors()
	            .stream()
	            .map(error ->
	                    error.getField() + ": " + error.getDefaultMessage()
	            )
	            .collect(Collectors.joining("; "));

	    ErrorResponse errorResponse = ErrorResponse.builder()
	            .status(HttpStatus.BAD_REQUEST.value())
	            .message(message)
	            .timestamp(LocalDateTime.now())
	            .build();

	    return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body(errorResponse);
	}
	@ExceptionHandler(InvalidTransactionTypeException.class)
	public ResponseEntity<ErrorResponse> handleInvalidTransactionType(
	        InvalidTransactionTypeException ex) {

	    ErrorResponse errorResponse = ErrorResponse.builder()
	            .status(HttpStatus.BAD_REQUEST.value())
	            .message(ex.getMessage())
	            .timestamp(LocalDateTime.now())
	            .build();

	    return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body(errorResponse);
	}
	
	
}