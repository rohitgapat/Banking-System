
package com.banking.Transaction.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.banking.Transaction.client.AccountClient;
import com.banking.Transaction.exception.TransactionNotFoundException;
import com.banking.Transaction.model.TransactionRequest;
import com.banking.Transaction.model.TransferRequest;
import com.banking.Transaction.service.TransactionService;

import org.springframework.http.MediaType;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ImportAutoConfiguration(exclude = FeignAutoConfiguration.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private AccountClient accountClient;

    @Test
    void shouldReturn404WhenTransactionNotFound() throws Exception {

        when(transactionService.getTransactionById(999L))
                .thenThrow(new TransactionNotFoundException(
                        "Transaction not found with id: 999"
                ));

        mockMvc.perform(get("/transactions/999"))
                .andExpect(status().isNotFound());
    }
    
    // 1. Create transaction - success
    @Test
    void createTransaction_whenRequestIsValid_shouldReturn201()
            throws Exception {

        when(transactionService.createTransaction(
                any(TransactionRequest.class)))
                .thenReturn(null);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "accountNumber": "12345678901",
                        "transactionType": "DEPOSIT",
                        "amount": 500.0
                    }
                    """))
                .andExpect(status().isCreated());
    }


    // 2. Create transaction - invalid request
    @Test
    void createTransaction_whenRequestInvalid_shouldReturn400()
            throws Exception {

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }


    // 3. Get transaction by ID - success
    @Test
    void getTransactionById_shouldReturn200() throws Exception {

        when(transactionService.getTransactionById(1L))
                .thenReturn(null);

        mockMvc.perform(get("/transactions/1"))
                .andExpect(status().isOk());
    }


    // 4. Get transaction by ID - not found
    @Test
    void getTransactionById_whenNotFound_shouldReturn404()
            throws Exception {

        when(transactionService.getTransactionById(999L))
                .thenThrow(new com.banking.Transaction.exception
                        .TransactionNotFoundException(
                                "Transaction not found with id: 999"));

        mockMvc.perform(get("/transactions/999"))
                .andExpect(status().isNotFound());
    }


    // 5. Get transaction by ID - invalid ID
    @Test
    void getTransactionById_whenIdIsNotNumeric_shouldReturn400()
            throws Exception {

        mockMvc.perform(get("/transactions/abc"))
                .andExpect(status().isBadRequest());
    }


    // 6. Get all transactions
    @Test
    void getAllTransactions_shouldReturn200() throws Exception {

        when(transactionService.getAllTransactions())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }


    // 7. Get transactions by account
    @Test
    void getTransactionsByAccount_shouldReturn200() throws Exception {

        when(transactionService.getTransactionsByAccount(
                "12345678901"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get(
                "/transactions/account/12345678901"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }


    // 8. Get account information - success
    @Test
    void getAccount_shouldReturn200() throws Exception {

        when(accountClient.getAccount("12345678901"))
                .thenReturn(null);

        mockMvc.perform(get(
                "/transactions/account-info/12345678901"))
                .andExpect(status().isOk());
    }


    // 9. Transfer - success
    @Test
    void transfer_shouldReturn201() throws Exception {

        when(transactionService.transfer(any(TransferRequest.class)))
                .thenReturn(null);

        mockMvc.perform(post("/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "fromAccountNumber": "12345678901",
                        "toAccountNumber": "12345678902",
                        "amount": 500
                    }
                    """))
                .andExpect(status().isCreated());
    }

}
